package filesystem;

import com.fileutils.specs2.models.FileSystem;
import com.fileutils.specs2.models.FileSystemException;
import usersystem.Manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFileSystem implements FileSystem {
    private Folder currentDirectory;
    private Folder root;
    private int commandNo;
    private static Manager manager = new Manager();

    {
        manager.setMyFileSystem(this);
    }

    private boolean isFatherOf(String s1, String s2) {
        //To see if path s1 isFather of s2
        String ss1 = Tool.simplifyPath(s1);
        String ss2 = Tool.simplifyPath(s2);
        if (ss1.equals("/")) {
            return true;
        }
        Pattern p = Pattern.compile("^" + ss1 + "(/|$)");
        Matcher m = p.matcher(ss2);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static Manager getManager() {
        return manager;
    }

    public int getCommandNo() {
        return commandNo;
    }

    public void setCommandNo(int commandNo) {
        this.commandNo = commandNo;
    }

    public MyFileSystem() {
        //root = new Folder("", new ArrayList<>(), 0, null);
        root = new Folder();
        currentDirectory = root;
        commandNo = 0;
    }

    public String getCurrentDirectory() {
        return Tool.pathToString(currentDirectory.getPath()) + "/" + currentDirectory.getName();
    }

    private void legalLength(String path) throws InputPathLengthExceed4096, FolderException {
        if (path.length() > 4096) {
            throw new InputPathLengthExceed4096(path);
        }
        /////////////////
//        String head = "^/*(([a-zA-Z]|\\.|_)([a-zA-Z]|\\.|_|[0-9]){0,255})?";
//        String body = "(/+([a-zA-Z]|\\.|_)([a-zA-Z]|\\.|_|[0-9]){0,255})*/*$";
//        Pattern p = Pattern.compile(head + body);
//        Matcher m = p.matcher(path);
//        if (!m.find()) {
//            throw new FolderException(path);
//        }
        /////////////////
    }

    private Folder getIn(Folder begin, List<String> path) throws FileSystemException {
        Folder tmp = begin;
        for (int i = 0; i < path.size() - 1; i++) {
            String p = path.get(i);
            try {
                //tmp = tmp.getToSonFolder(p);
                tmp = tmp.getToSonFolderWithRedirect(p, root);
            } catch (NullPointerException e) {
                //throw new FolderException(Tool.pathToString(paths));
                throw new NullPointerException();
            } catch (IsFileException e) {
                throw new NullPointerException();
            }
        }
        return tmp;
    }

    @Override
    public String linkHard(String s, String s1) throws FileSystemException {
        commandNo++;
        legalLength(s);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(s)) {
            tmp = root;
        }
        List<String> path1 = Tool.getDirectories(s);
        if (path1.size() == 0) {
            throw new FolderException(s);
        }
        String filename = path1.get(path1.size() - 1);

        try {
            tmp = getIn(tmp, path1);
        } catch (NullPointerException e) {
            throw new FolderException(s);
        }

        File dst;
        boolean isFolder = true;

        try {
            tmp = tmp.getToSonFolderWithRedirect(filename, root);
        } catch (IsFileException e) {
            isFolder = false;
            dst = e.getFile();
            tmp = e.getFather();
            filename = dst.getName();
        } catch (NullPointerException e) {
            throw new FolderException(s);
        }
        if (isFolder) {
            throw new FolderException(s);
        }

        if (tmp.getFileContains().containsKey(filename)) {
            legalLength(s1);
            dst = tmp.getFileContains().get(filename);
            Folder tmp2 = currentDirectory;
            if (Tool.isAbsolutePath(s)) {
                tmp2 = root;
            }
            List<String> path2 = Tool.getDirectories(s1);
//            if (path2.size() == 0) {
//                throw new FolderException(s1);
//            }
            String fname = path2.size() == 0 ? "" : path2.get(path2.size() - 1);
            try {
                tmp2 = getIn(tmp2, path2);
            } catch (NullPointerException e) {
                throw new FolderException(s1);
            }

            //when fname is a soft link
            if (tmp2.getFileContains().containsKey(fname)
                    && tmp2.getFileContains().get(fname).getType() == 1) {
                try {
                    tmp2 = tmp2.getToSonFolderWithRedirect(fname, root);
                } catch (IsFileException e) {
                    //We should see if srcpath and dstpath are the same path
                    if (concatPath(tmp, filename).equals(concatPath(e.getFather(), e.getFile().getName()))) {
                        throw new FolderException(s1);
                    }
                    throw new AlreadyExistException(s1);
                }//impossible to happen NullPointer

                if (tmp2.getFileContains().containsKey(filename) || tmp2.getSonFolder().containsKey(filename)) {
                    throw new AlreadyExistException(s1 + "/" + filename);
                }
                //So we can make a link
                //name: filename
                File f = null;

//                    f = new File(filename, Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName(),
//                            commandNo, dst.getData());
                f = new File(filename, tmp2, commandNo, dst.getData());

                tmp2.getFileContains().put(filename, f);
                tmp2.setModifiedTime(commandNo);
                return concatPath(tmp, filename);

            }
            //when fname is a normal file
            else if (tmp2.getFileContains().containsKey(fname) && tmp2.getFileContains().get(fname).getType() != 1) {
                throw new AlreadyExistException(s1);
            }
            //when fname is a existing foleder
            else if (tmp2.getSonFolder().containsKey(fname)) {
                tmp2 = tmp2.getToSonFolder(fname);
                //Now tmp2 is where we want to make a hard link
                //srcpath is sure to be file, so srcpath is not father of dstpath
                if (!tmp2.getFileContains().containsKey(filename) &&
                        !tmp2.getSonFolder().containsKey(filename)) {
                    File f;

//                        f = new File(filename, Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName(),
//                                commandNo, dst.getData());
                    f = new File(filename, tmp2, commandNo, dst.getData());

                    tmp2.setModifiedTime(commandNo);
                    tmp2.getFileContains().put(filename, f);
                    return concatPath(tmp, filename);
                } else {
                    throw new AlreadyExistException(s1 + "/" + filename);
                }
                //finish case
            }
            //when fname doesn't exist
            else {
                File f;

//                    f = new File(fname, Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName(),
//                            commandNo, dst.getData());
                f = new File(fname, tmp2, commandNo, dst.getData());

                tmp2.getFileContains().put(fname, f);
                tmp2.setModifiedTime(commandNo);
                return concatPath(tmp, filename);
            }
        } else {
            throw new FolderException(s);
        }

    }

    @Override
    public void move(String s, String s1) throws FileSystemException {
        commandNo++;
        legalLength(s);
        Folder tmp1 = currentDirectory;
        if (Tool.isAbsolutePath(s)) {
            tmp1 = root;
        }
        List<String> paths = Tool.getDirectories(s);
        try {
            tmp1 = getIn(tmp1, paths);
        } catch (NullPointerException e) {
            throw new FolderException(s);
        }
        String fname1 = (paths.isEmpty()) ? "" : paths.get(paths.size() - 1);
        if (fname1.equals(".") || fname1.equals("..")) {
            tmp1 = tmp1.getToSonFolder(fname1);
            fname1 = tmp1.getName();
            tmp1 = tmp1.getFather();
        }

        if (tmp1.getFileContains().containsKey(fname1) || tmp1.getSonFolder().containsKey(fname1)) {
            if (isFatherOf(concatPath(tmp1, fname1), concatPath(currentDirectory.getFather(), currentDirectory.getName()))) {
                throw new FolderException(s);
            }
            legalLength(s1);
            Folder tmp2 = currentDirectory;
            if (Tool.isAbsolutePath(s1)) {
                tmp2 = root;
            }
            List<String> paths2 = Tool.getDirectories(s1);
            try {
                tmp2 = getIn(tmp2, paths2);
            } catch (NullPointerException e) {
                throw new FolderException(s);
            }
            String fname2 = (paths2.isEmpty()) ? "" : paths2.get(paths2.size() - 1);
            if (fname2.equals(".") || fname2.equals("..")) {
                tmp2 = tmp2.getToSonFolder(fname2);
                fname2 = tmp2.getName();
                tmp2 = tmp2.getFather();
            }

            //when not exist
            if (!(tmp2.getFileContains().containsKey(fname2) || tmp2.getSonFolder().containsKey(fname2))) {
                if (isFatherOf(concatPath(tmp1, fname1), concatPath(tmp2, fname2))) {
                    throw new FolderException(s1);
                }
                //if src is a file
                if (tmp1.getFileContains().containsKey(fname1)) {
                    File f = tmp1.getFileContains().get(fname1);
                    tmp1.getFileContains().remove(fname1);
                    tmp2.getFileContains().put(fname2, f);
                    f.setName(fname2);
                    f.setFather(tmp2);
                    tmp2.setModifiedTime(commandNo);
                    f.setModify_time(commandNo);
                    return;
                }
                //if src is a folder
                else {
                    Folder f = tmp1.getSonFolder().get(fname1);
                    tmp1.getSonFolder().remove(fname1);
                    tmp2.getSonFolder().put(fname2, f);
                    f.setName(fname2);
                    f.setFather(tmp2);
                    tmp2.setModifiedTime(commandNo);
                    f.setModifiedTime(commandNo);
                    for (Folder fd : f.getSonFolder().values()) {
                        fd.setModifiedTime(commandNo);
                    }
                    return;
                }
            }
            //when src is a file AND dst is a folder
            if (tmp1.getFileContains().containsKey(fname1) && tmp2.getSonFolder().containsKey(fname2)) {
                tmp2 = tmp2.getToSonFolder(fname2);
                if (!(tmp2.getFileContains().containsKey(fname1) || tmp2.getSonFolder().containsKey(fname1))) {
                    File f = tmp1.getFileContains().get(fname1);
                    tmp1.getFileContains().remove(fname1);
                    tmp2.getFileContains().put(fname1, f);
                    f.setFather(tmp2);
                    tmp2.setModifiedTime(commandNo);
                    f.setModify_time(commandNo);
                    return;
                } else if (tmp2.getFileContains().containsKey(fname1)) {
                    File f = tmp1.getFileContains().get(fname1);
                    tmp1.getFileContains().remove(fname1);
                    tmp2.getFileContains().put(fname1, f);
                    f.setModify_time(commandNo);
                    f.setFather(tmp2);
                    //////////////////////////////////
                    tmp2.setModifiedTime(commandNo);
                    return;
                } else {
                    throw new AlreadyExistException(s1 + "/" + fname1);
                }
            }
            //when src is a file and dst is a file
            else if (tmp1.getFileContains().containsKey(fname1) && tmp2.getFileContains().containsKey(fname2)) {
                if (concatPath(tmp1, fname1).equals(concatPath(tmp2, fname2))) {
                    throw new FolderException(s1);
                }
                File f = tmp1.getFileContains().get(fname1);
                tmp1.getFileContains().remove(fname1);
                tmp2.getFileContains().remove(fname2);
                f.setModify_time(commandNo);
                f.setFather(tmp2);
                tmp2.getFileContains().put(fname1, f);
                /////////////////////
                tmp2.setModifiedTime(commandNo);
                return;
            }
            //when src is a folder and dst is a folder
            else if (tmp1.getSonFolder().containsKey(fname1) && tmp2.getSonFolder().containsKey(fname2)) {
                if (isFatherOf(concatPath(tmp1, fname1), concatPath(tmp2, fname2))) {
                    throw new FolderException(s1);
                }
                tmp2 = tmp2.getToSonFolder(fname2);
                if (!(tmp2.getSonFolder().containsKey(fname1) || tmp2.getFileContains().containsKey(fname1))) {
                    Folder f = tmp1.getSonFolder().get(fname1);
                    tmp1.getSonFolder().remove(fname1);
                    tmp2.getSonFolder().put(fname1, f);
                    f.setFather(tmp2);
                    tmp2.setModifiedTime(commandNo);
                    f.setModifiedTime(commandNo);
                    for (Folder fd : f.getSonFolder().values()) {
                        fd.setModifiedTime(commandNo);
                    }
                    return;
                } else if (tmp2.getSonFolder().containsKey(fname1)) {
                    Folder fd = tmp2.getSonFolder().get(fname1);
                    if (fd.getSonFolder().size() == 2 && fd.getFileContains().size() == 0) {
                        Folder f = tmp1.getSonFolder().get(fname1);
                        tmp1.getSonFolder().remove(fname1);
                        tmp2.getSonFolder().put(fname2, f);
                        f.setFather(tmp2);
                        tmp2.setModifiedTime(commandNo);
                        f.setModifiedTime(commandNo);
                        for (Folder ffd : f.getSonFolder().values()) {
                            ffd.setModifiedTime(commandNo);
                        }
                        return;
                    } else {
                        throw new AlreadyExistException(s1 + "/" + fname1);
                    }
                } else if (tmp2.getFileContains().containsKey(fname1)) {
                    throw new AlreadyExistException(s1 + "/" + fname1);
                }
            }
            //when src is a folder and dst is a file
            else {
                throw new AlreadyExistException(s1);
            }
        } else {
            throw new FolderException(s);
        }


    }

    @Override
    public void copy(String s, String s1) throws FileSystemException {
        commandNo++;
        legalLength(s);
        Folder tmp = currentDirectory;
        Folder tmp2 = currentDirectory;
        if (Tool.isAbsolutePath(s)) {
            tmp = root;
        }
        List<String> paths = Tool.getDirectories(s);
        try {
            tmp = getIn(tmp, paths);
        } catch (NullPointerException e) {
            throw new FolderException(s);
        }
        String src = (paths.isEmpty()) ? "" : paths.get(paths.size() - 1);
        if (src.equals(".") || src.equals("..")) {
            tmp = tmp.getToSonFolder(src);
            src = tmp.getName();
            tmp = tmp.getFather();
        }
        if (isFatherOf(concatPath(tmp, src), concatPath(currentDirectory.getFather(), currentDirectory.getName()))) {
            throw new FolderException(s1);
        }
        if (!(tmp.getFileContains().containsKey(src) || tmp.getSonFolder().containsKey(src))) {
            throw new FolderException(s);
        } else {
            legalLength(s1);
            if (Tool.isAbsolutePath(s1)) {
                tmp2 = root;
            }
            List<String> paths2 = Tool.getDirectories(s1);
            try {
                tmp2 = getIn(tmp2, paths2);
            } catch (NullPointerException e) {
                throw new FolderException(s1);
            }
            String dst = (paths2.isEmpty()) ? "" : paths2.get(paths2.size() - 1);
            if (dst.equals(".") || dst.equals("..")) {
                tmp2 = tmp2.getToSonFolder(dst);
                dst = tmp2.getName();
                tmp2 = tmp2.getFather();
            }
            if (!(tmp2.getFileContains().containsKey(dst) || tmp2.getSonFolder().containsKey(dst))) {
                if (isFatherOf(concatPath(tmp, src), concatPath(tmp2, dst))) {
                    throw new FolderException(s1);
                }
                if (tmp.getFileContains().containsKey(src)) {
                    try {
                        File newFile = tmp.getFileContains().get(src).deepClone();
                        newFile.setCreateTime(commandNo);
                        newFile.setModify_time(commandNo);
                        newFile.setFather(tmp2);

                        newFile.setName(dst);

                        tmp2.getFileContains().put(newFile.getName(), newFile);
                        tmp2.setModifiedTime(commandNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Folder newFolder = tmp.getSonFolder().get(src).deepClone();
                        newFolder.setModifiedTime(commandNo);
                        newFolder.setCreateTime(commandNo);
                        newFolder.setFather(tmp2);
                        //////////////////
                        for (File files : newFolder.getFileContains().values()) {
                            files.setModify_time(commandNo);
                            files.setCreateTime(commandNo);
                        }
                        for (String folders : newFolder.getSonFolder().keySet()) {
                            if (folders.equals(".") || folders.equals("..") || folders.equals("")) {
                                continue;
                            }
                            newFolder.getSonFolder().get(folders).setModifiedTime(commandNo);
                            newFolder.getSonFolder().get(folders).setCreateTime(commandNo);
                        }
                        /////////////////
                        newFolder.setName(dst);

                        tmp2.getSonFolder().put(dst, newFolder);
                        tmp2.setModifiedTime(commandNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if (tmp.getFileContains().containsKey(src)) {
                if (isFatherOf(concatPath(tmp, src), concatPath(tmp2, dst))) {
                    throw new FolderException(s1);
                }
                if (tmp2.getSonFolder().containsKey(dst)) {
                    tmp2 = tmp2.getToSonFolder(dst);
                    if (tmp2.getSonFolder().containsKey(src)) {
                        throw new AlreadyExistException(s1 + "/" + src);
                    }
                    try {
                        ////File newFile = tmp.getFileContains().get(src).deepClone();
                        File newFile;
                        if (tmp2.getFileContains().containsKey(src)) {
                            newFile = tmp.getFileContains().get(src);
                            ////newFile.setCreateTime(tmp2.getFileContains().get(src).getData().getCreate_time());
                            tmp2.getFileContains().get(src).writeFile(newFile.getData().getContent(), commandNo);
                            newFile=tmp2.getFileContains().get(src);
                        } else {
                            newFile = tmp.getFileContains().get(src).deepClone();
                            newFile.setCreateTime(commandNo);
                        }
                        newFile.setModify_time(commandNo);
                        newFile.setFather(tmp2);
                        tmp2.getFileContains().put(newFile.getName(), newFile);
                        ////////////////////////
                        tmp2.setModifiedTime(commandNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (tmp2.getFileContains().containsKey(dst)) {
                    try {
                        //File newFile = tmp.getFileContains().get(src).deepClone();
                        File newFile = tmp2.getFileContains().get(dst);
                        newFile.writeFile(tmp.getFileContains().get(src).getData().getContent(),commandNo);
                        //////////////////
                        //newFile.setName(tmp2.getFileContains().get(dst).getName());
                        ////////////
                        newFile.setModify_time(commandNo);
                        newFile.setFather(tmp2);
                        tmp2.getFileContains().put(newFile.getName(), newFile);
                        /////////////////////////
                        tmp2.setModifiedTime(commandNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if (tmp.getSonFolder().containsKey(src)) {
                if (isFatherOf(concatPath(tmp, src), concatPath(tmp2, dst))) {
                    throw new FolderException(s1);
                }
                if (tmp2.getSonFolder().containsKey(dst)) {
                    tmp2 = tmp2.getToSonFolder(dst);
                    if (!(tmp2.getSonFolder().containsKey(src) || tmp2.getFileContains().containsKey(src))) {
                        try {
                            Folder newFolder = tmp.getSonFolder().get(src).deepClone();
                            newFolder.setCreateTime(commandNo);
                            newFolder.setModifiedTime(commandNo);
                            newFolder.setFather(tmp2);
                            tmp2.getSonFolder().put(src, newFolder);
                            tmp2.setModifiedTime(commandNo);
                            //////////////////
                            for (File files : newFolder.getFileContains().values()) {
                                files.setModify_time(commandNo);
                                files.setCreateTime(commandNo);
                            }
                            for (String folders : newFolder.getSonFolder().keySet()) {
                                if (folders.equals(".") || folders.equals("..") || folders.equals("")) {
                                    continue;
                                }
                                newFolder.getSonFolder().get(folders).setModifiedTime(commandNo);
                                newFolder.getSonFolder().get(folders).setCreateTime(commandNo);
                            }
                            ///////////////////

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (tmp2.getSonFolder().containsKey(src)) {
                        if (tmp2.getSonFolder().get(src).getSonFolder().size() == 2 && tmp2.getSonFolder().get(src).getFileContains().isEmpty()) {
                            try {
                                Folder newFolder = tmp.getSonFolder().get(src).deepClone();
                                newFolder.setCreateTime(tmp2.getSonFolder().get(src).getCreateTime());
                                newFolder.setUser(tmp2.getSonFolder().get(src).getUser());
                                newFolder.setGroup(tmp2.getSonFolder().get(src).getGroup());
                                newFolder.setModifiedTime(commandNo);
                                newFolder.setFather(tmp2);
                                tmp2.getSonFolder().put(src, newFolder);
                                ////////////////
                                tmp2.setModifiedTime(commandNo);
                                /////////////////////
                                for (File files : newFolder.getFileContains().values()) {
                                    files.setModify_time(commandNo);
                                    files.setCreateTime(commandNo);
                                }
                                for (String folders : newFolder.getSonFolder().keySet()) {
                                    if (folders.equals(".") || folders.equals("..") || folders.equals("")) {
                                        continue;
                                    }
                                    newFolder.getSonFolder().get(folders).setModifiedTime(commandNo);
                                    newFolder.getSonFolder().get(folders).setCreateTime(commandNo);
                                }
                                ///////////////////
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            throw new AlreadyExistException(s1 + "/" + src);
                        }
                    } else {
                        throw new AlreadyExistException(s1 + "/" + src);
                    }
                } else if (tmp2.getFileContains().containsKey(dst)) {
                    throw new AlreadyExistException(s1);
                }
            }
        }
    }


    private String concatPath(Folder fd, String fname) {
        String result;
        if (fd.getName().equals("")) {
            result = "/" + fname;
        } else {
            result = Tool.pathToString(fd.getPath()) + "/" + fd.getName() + "/" + fname;
        }
        return result;
    }

    @Override
    public String linkSoft(String s, String s1) throws FileSystemException {
        commandNo++;
        legalLength(s);

        Folder tmp1 = currentDirectory;
        if (Tool.isAbsolutePath(s)) {
            tmp1 = root;
        }
        List<String> path1 = Tool.getDirectories(s);
        String fname1 = path1.size() == 0 ? "" : path1.get(path1.size() - 1);
        try {
            tmp1 = getIn(tmp1, path1);
        } catch (NullPointerException e) {
            throw new FolderException(s);        //srcpath is invalid
        }
        //Now tmp1 is the father of what we want
        try {
            tmp1 = tmp1.getToSonFolderWithRedirect(fname1, root);
            fname1 = tmp1.getName();
            tmp1 = tmp1.getFather();
        } catch (NullPointerException e) {
            throw new FolderException(s);            //srcpath is invalid
        } catch (IsFileException e) {
            File tmpfile = e.getFile();
            tmp1 = e.getFather();
            fname1 = tmpfile.getName();
        }
        //Now tmp1 is the father of whom named fname1
        //And srcPath is sure to exist from now on
        //But we do not know fname is a file or a folder
        legalLength(s1);
        Folder tmp2 = currentDirectory;
        if (Tool.isAbsolutePath(s1)) {
            tmp2 = root;
        }
        List<String> path2 = Tool.getDirectories(s1);
        String fname2 = path2.size() == 0 ? "" : path2.get(path2.size() - 1);
        try {
            tmp2 = getIn(tmp2, path2);
        } catch (NullPointerException e) {
            throw new FolderException(s1);             //dstpath is invalid
        }
        //Now tmp2 is the father of what we want

        //First think of fname2 is a softlink
        if (tmp2.getFileContains().containsKey(fname2)
                && tmp2.getFileContains().get(fname2).getType() == 1) {
            //fname2 is a soft link
            try {
                tmp2 = tmp2.getToSonFolderWithRedirect(fname2, root);
                fname2 = tmp2.getName();
            } /*catch (NullPointerException e) {
                //impossible to happen
                System.out.println("There must be something wrong");
            }*/ catch (IsFileException e) {
                //Obviously there should be a AlreadyExistException
                //But if srcpath and dstpath points the same, we should handle this first
                //srcpath=tmp1's path + / + tmp1's name + / + fname1 AND
                //if tmp1 is root(means tmp1.name is ""), srcpath should be /fname1
                //So do tmp2
                //And now function hasSamePath can do this
                tmp2 = e.getFather();
                fname2 = e.getFile().getName();
                String src;
                String dst;
                if (tmp1.getName().equals("")) {
                    src = "/" + fname1;
                } else {
                    src = Tool.pathToString(tmp1.getPath()) + "/" + tmp1.getName() + "/" + fname1;
                }
                if (tmp2.getName().equals("")) {
                    dst = "/" + fname2;
                } else {
                    dst = Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName() + "/" + fname2;
                }
                if (src.equals(dst)) {
                    throw new FolderException(s1);
                }
                //Now we can throw AlreadyExistException
                throw new AlreadyExistException(s1);
            }
            //if succeed, then tmp2 is the folder that we will make a softlink in
            //But first we still should judge if srcpath and dstpath are the same
            if (concatPath(tmp1, fname1).equals(concatPath(tmp2.getFather(), fname2))) {
                throw new FolderException(s1);
            }
            //THEN we should think of if srcpath is father of dstpath
            if (isFatherOf(concatPath(tmp1, fname1), concatPath(tmp2.getFather(), fname2))) {
                throw new FolderException(s1);
            }
            //FINALLY we should think of if tmp2 contains a file or a folder
            // having the same name as **fname1**
            if (tmp2.getFileContains().containsKey(fname1) || tmp2.getSonFolder().containsKey(fname1)) {
                throw new AlreadyExistException(s1 + "/" + fname1);
                //NOTICE that fname1 musn't be root. If so, Exception will appear before
            }

            //Now we can make a softlink happily
            //name:fname1
            File f = null;
            //try {
//                f = new File(fname1, Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName(),
//                        commandNo, concatPath(tmp1, fname1));
            f = new File(fname1, tmp2, commandNo, concatPath(tmp1, fname1), manager.getUser(), manager.getGroup());
            //} catch (FileSystemException e) {
                /*System.out.println("There must be something wrong.");
                throw new FolderException(s1);*/
            // }
            //Now we should update its father
            tmp2.setModifiedTime(commandNo);
            tmp2.getFileContains().put(fname1, f);
            //this case finish(fname2 is a softlink)
            return concatPath(tmp1, fname1);
        }
        //then think of fname2 is a existing file(except softlink)
        else if (tmp2.getFileContains().containsKey(fname2)
                && tmp2.getFileContains().get(fname2).getType() != 1) {
            throw new AlreadyExistException(s1);
        }
        //then think of fname2 is a existing folder
        else if (tmp2.getSonFolder().containsKey(fname2)) {
            tmp2 = tmp2.getToSonFolder(fname2);
            //tmp2 is the folder we will make a softlink in
            //First we should be sure that srcpath and dstpath are not same
            if (concatPath(tmp1, fname1).equals(concatPath(tmp2.getFather(), fname2))) {
                throw new FolderException(s1);
            }
            //Then we check if srcpath is father of dstpath
            if (isFatherOf(concatPath(tmp1, fname1), concatPath(tmp2.getFather(), fname2))) {
                throw new FolderException(s1);
            }
            //FINALLY we should think of if tmp2 contains a file or a folder
            // having the same name as **fname1**
            if (tmp2.getFileContains().containsKey(fname1) || tmp2.getSonFolder().containsKey(fname1)) {
                throw new AlreadyExistException(s1 + "/" + fname1);
                //NOTICE that fname1 musn't be root. If so, Exception will appear before
            }
            //Now we can make a softlink happily
            //name:fname1
            File f = null;
            //try {
//                f = new File(fname1, Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName(),
//                        commandNo, concatPath(tmp1, fname1));
            f = new File(fname1, tmp2, commandNo, concatPath(tmp1, fname1), manager.getUser(), manager.getGroup());
            /*} catch (FileSystemException e) {
              System.out.println("There must be something wrong.");
                throw new FolderException(s1);
            }*/
            //Now we should update its father
            tmp2.setModifiedTime(commandNo);
            tmp2.getFileContains().put(fname1, f);
            //this case finish(fname2 is a existing folder)
            return concatPath(tmp1, fname1);
        }
        //then think fname2 doesn't exist
        else {
            //In this case, srcpath and dstpath can't be same
            //But srcpath still can be father of dstpath
            if (isFatherOf(concatPath(tmp1, fname1), concatPath(tmp2, fname2))) {
                throw new FolderException(s1);
            }
            //Now we can make a softlink happily
            //name:fname2
            File f = null;
            //try {
//                f = new File(fname2, Tool.pathToString(tmp2.getPath()) + "/" + tmp2.getName(),
//                        commandNo, concatPath(tmp1, fname1));
            f = new File(fname2, tmp2, commandNo, concatPath(tmp1, fname1), manager.getUser(), manager.getGroup());
            // } //catch (FileSystemException e) {
                /*System.out.println("There must be something wrong.");
                throw new FolderException(s1);*/
            //}
            tmp2.setModifiedTime(commandNo);
            tmp2.getFileContains().put(fname2, f);
            //All case finish. GOOD JOB!
            return concatPath(tmp1, fname1);
        }
    }


    @Override
    public String readLink(String s) throws FileSystemException {
        commandNo++;
        legalLength(s);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(s)) {
            tmp = root;
        }
        List<String> path = Tool.getDirectories(s);
        if (path.size() == 0) {
            throw new FolderException(s);
        }
        String name = path.get(path.size() - 1);
        try {
            tmp = getIn(tmp, path);
        } catch (NullPointerException e) {
            throw new FolderException(s);
        }
        if (tmp.getFileContains().containsKey(name) && tmp.getFileContains().get(name).getType() == 1) {
            return Tool.simplifyPath(tmp.getFileContains().get(name).getData().getContent());
        } else {
            throw new FolderException(s);
        }
    }


    //    private void folderModified(int time, List<String> path) {
//        /*if (!path.get(0).equals("")) {
//            System.out.println("There must be Something wrong");
//        }*/
//
//        Folder tmp = root;
//        tmp.setModifiedTime(time);
//        for (int i = 1; i < path.size(); i++) {
//            try {
//                tmp = tmp.getToSonFolder(path.get(i));
//            } catch (NullPointerException e) {
//                return;
//            }
//            tmp.setModifiedTime(time);
//        }
//    }


    private void folderModified(int time, Folder f) {
        f.setModifiedTime(time);
        // f.getFather().setModifiedTime(time);
    }

    @Override
    public String changeDirectory(String path) throws FileSystemException {
        commandNo++;
        legalLength(path);
        List<String> paths = Tool.getDirectories(path);
        Folder ans = currentDirectory;
        if (Tool.isAbsolutePath(path)) {
            currentDirectory = root;
        }
        for (String directory : paths) {
            try {
                currentDirectory = currentDirectory.getToSonFolderWithRedirect(directory, root);
            } catch (NullPointerException | IsFileException e) {
                currentDirectory = ans;
                throw new FolderException(path);
            }
        }
        return Tool.pathToString(currentDirectory.getPath()) + "/" + currentDirectory.getName();
    }

    @Override
    public void touchFile(String filePath) throws FileSystemException {
        commandNo++;
        legalLength(filePath);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(filePath)) {
            tmp = root;
        }
        List<String> paths = Tool.getDirectories(filePath);
        if (paths.size() == 0) {
            throw new FolderException(filePath);
        }
        String filename = paths.get(paths.size() - 1);
        for (int i = 0; i < paths.size() - 1; i++) {
            String p = paths.get(i);
            try {
                tmp = tmp.getToSonFolderWithRedirect(p, root);
            } catch (NullPointerException | IsFileException e) {
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(filePath);
            }
        }
        if (tmp.getSonFolder().containsKey(filename)) {
            throw new FolderException(filePath);
        }
        if (tmp.getFileContains().containsKey(filename)
                && tmp.getFileContains().get(filename).getType() != 1) {
            tmp.getFileContains().get(filename).setModify_time(commandNo);
            return;
        } else if (tmp.getFileContains().containsKey(filename) &&
                tmp.getFileContains().get(filename).getType() == 1) {
            try {
                tmp = tmp.getToSonFolderWithRedirect(filename, root);
            } catch (IsFileException e) {
                e.getFile().setModify_time(commandNo);
                return;
            }
            throw new FolderException(filePath);
        } else {
            File f;
            try {
//                f = new File(filename, Tool.pathToString(tmp.getPath()) + "/" + tmp.getName(), commandNo);
                f = new File(filename, tmp, commandNo, manager.getUser(), manager.getGroup());
            } catch (FileSystemException e) {
                throw new FolderException(filePath);
            }
            List<String> pathtomodify = new ArrayList<>(tmp.getPath());
            pathtomodify.add(tmp.getName());
            //folderModified(commandNo, pathtomodify);
            folderModified(commandNo, tmp);
            tmp.getFileContains().put(filename, f);
        }
    }

    @Override
    public String catFile(String filePath) throws FileSystemException {
        commandNo++;
        legalLength(filePath);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(filePath)) {
            tmp = root;
        }
        List<String> paths = Tool.getDirectories(filePath);
        if (paths.size() == 0) {
            throw new FolderException(filePath);
        }
        String filename = paths.get(paths.size() - 1);
        for (int i = 0; i < paths.size() - 1; i++) {
            String p = paths.get(i);
            try {
                tmp = tmp.getToSonFolderWithRedirect(p, root);
            } catch (NullPointerException | IsFileException e) {
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(filePath);
            }
        }
        if (tmp.getFileContains().containsKey(filename)
                && tmp.getFileContains().get(filename).getType() != 1) {
            return tmp.getFileContains().get(filename).outputFile();
        } else if (tmp.getFileContains().containsKey(filename)
                && tmp.getFileContains().get(filename).getType() == 1) {
            try {
                tmp.getToSonFolderWithRedirect(filename, root);
            } catch (IsFileException e) {
                return e.getFile().outputFile();
            }
            throw new FolderException(filePath);
        } else {
            throw new FolderException(filePath);
        }

    }

    @Override
    public String list(String filePath) throws FileSystemException {
        commandNo++;
        legalLength(filePath);
        Folder ans = currentDirectory;
        List<String> paths = Tool.getDirectories(filePath);
        if (Tool.isAbsolutePath(filePath)) {
            currentDirectory = root;
        }
        for (String directory : paths) {
            try {
                currentDirectory = currentDirectory.getToSonFolderWithRedirect(directory, root);
            } catch (NullPointerException | IsFileException e) {
                currentDirectory = ans;
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(filePath);
            }
        }
        String ret = "";
        ArrayList<String> list = new ArrayList<>(currentDirectory.getSonFolder().keySet());
        list.addAll(currentDirectory.getFileContains().keySet());
        Collections.sort(list);
        for (String s : list) {
            if (s.equals(".") || s.equals("..") || s.equals("")) {
                continue;
            }
            ret = ret.concat(s + " ");
        }
        currentDirectory = ans;
        return ret;
    }

    @Override
    public void fileWrite(String filePath, String content) throws FileSystemException {
        commandNo++;
        legalLength(filePath);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(filePath)) {
            tmp = root;
        }
        List<String> paths = Tool.getDirectories(filePath);
        if (paths.size() == 0) {
            //throw new FolderException("/");
            throw new FolderException(filePath);
        }
        String filename = paths.get(paths.size() - 1);
        for (int i = 0; i < paths.size() - 1; i++) {
            String p = paths.get(i);
            try {
                tmp = tmp.getToSonFolderWithRedirect(p, root);
            } catch (NullPointerException | IsFileException e) {
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(filePath);
            }
        }
        if (tmp.getFileContains().containsKey(filename) &&
                tmp.getFileContains().get(filename).getType() != 1) {
            tmp.getFileContains().get(filename).writeFile(content, commandNo);
        } else if (tmp.getFileContains().containsKey(filename) &&
                tmp.getFileContains().get(filename).getType() == 1) {
            try {
                tmp.getToSonFolderWithRedirect(filename, root);
            } catch (IsFileException e) {
                e.getFile().writeFile(content, commandNo);
                return;
            }
            throw new FolderException(filePath);
        } else {
            if (tmp.getSonFolder().containsKey(filename)) {
                throw new FolderException(filePath);
            }
            File f;
            try {
//                f = new File(filename, Tool.pathToString(tmp.getPath()) + "/" + tmp.getName(), commandNo);
                f = new File(filename, tmp, commandNo, manager.getUser(), manager.getGroup());
                folderModified(commandNo, tmp);
            } catch (FileSystemException e) {
                throw new FolderException(filePath);
            }

            tmp.getFileContains().put(filename, f);
            f.writeFile(content, commandNo);
        }

    }

    @Override
    public void fileAppend(String filePath, String content) throws FileSystemException {
        commandNo++;
        legalLength(filePath);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(filePath)) {
            tmp = root;
        }
        List<String> paths = Tool.getDirectories(filePath);
        if (paths.size() == 0) {
            //throw new FolderException("/");
            throw new FolderException(filePath);
        }
        String filename = paths.get(paths.size() - 1);
        for (int i = 0; i < paths.size() - 1; i++) {
            String p = paths.get(i);
            try {
                tmp = tmp.getToSonFolderWithRedirect(p, root);
            } catch (NullPointerException | IsFileException e) {
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(filePath);
            }
        }
        if (tmp.getFileContains().containsKey(filename)
                && tmp.getFileContains().get(filename).getType() != 1) {
            tmp.getFileContains().get(filename).appendFile(content, commandNo);
        } else if (tmp.getFileContains().containsKey(filename)
                && tmp.getFileContains().get(filename).getType() == 1) {
            try {
                tmp.getToSonFolderWithRedirect(filename, root);
            } catch (IsFileException e) {
                e.getFile().appendFile(content, commandNo);
                return;
            }
            throw new FolderException(filePath);
        } else {
            if (tmp.getSonFolder().containsKey(filename)) {
                throw new FolderException(filePath);
            }
            File f;
            try {
//                f = new File(filename, Tool.pathToString(tmp.getPath()) + "/" + tmp.getName(), commandNo);
                f = new File(filename, tmp, commandNo, manager.getUser(), manager.getGroup());
                folderModified(commandNo, tmp);
            } catch (FileSystemException e) {
                throw new FolderException(filePath);
            }
            tmp.getFileContains().put(filename, f);
            f.appendFile(content, commandNo);
        }
    }

    @Override
    public String makeDirectory(String path) throws FileSystemException {
        commandNo++;
        legalLength(path);
        Folder ans = currentDirectory;
        List<String> paths = Tool.getDirectories(path);
        if (Tool.isAbsolutePath(path)) {
            currentDirectory = root;
        }
        if (paths.size() == 0) {
            //throw new FolderException("/");
            throw new AlreadyExistException(path);
        }
        for (int i = 0; i < paths.size() - 1; i++) {
            try {
                currentDirectory = currentDirectory.getToSonFolderWithRedirect(paths.get(i), root);
            } catch (NullPointerException e) {
                currentDirectory = ans;
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(path);
            } catch (IsFileException e) {
                currentDirectory = ans;
                throw new FolderException(path);
            }
        }
        if (currentDirectory.getSonFolder().containsKey(paths.get(paths.size() - 1))) {
            currentDirectory = ans;
            throw new AlreadyExistException(path);
        }
        if (currentDirectory.getFileContains().containsKey(paths.get(paths.size() - 1))) {
            try {
                currentDirectory.getToSonFolderWithRedirect(paths.get(paths.size() - 1), root);
            } /*catch (NullPointerException e) {
                currentDirectory = ans;
                throw new FolderException(path);
            }*/ catch (IsFileException e) {
                currentDirectory = ans;
                throw new FolderException(path);
            }
            currentDirectory = ans;
            throw new AlreadyExistException(path);
        }
        ArrayList<String> newPath = new ArrayList<>(currentDirectory.getPath());
        newPath.add(currentDirectory.getName());
        if (!paths.get(paths.size() - 1).matches("^[a-zA-Z._][a-zA-Z._0-9]{0,255}$")) {
            currentDirectory = ans;
            //throw new FolderException(Tool.pathToString(paths));
            throw new FolderException(path);
        }
        Folder f;
        ///try{
        f = new Folder(paths.get(paths.size() - 1), commandNo, currentDirectory, manager.getUser(), manager.getGroup());
        /*}catch (FolderException e) {
            currentDirectory = ans;
            throw new FolderException(path);
        }*/
        currentDirectory.getSonFolder().put(paths.get(paths.size() - 1), f);
        //currentDirectory.getSonFolder().put(paths.get(paths.size() - 1), new Folder(paths.get(paths.size() - 1),
        //        newPath, commandNo, currentDirectory));
        String ret = Tool.pathToString(newPath) + "/" + paths.get(paths.size() - 1);
        //folderModified(commandNo, newPath);
        folderModified(commandNo, currentDirectory);

        currentDirectory = ans;
        //currentDirectory.setModifiedTime(commandNo);
        return ret;
    }

    private void recoverBuiltFolders(List<Folder> made) {
        for (Folder f : made) {
            f.getFather().getSonFolder().remove(f.getName());
        }
    }

    @Override
    public String makeDirectoryRecursively(String path) throws FileSystemException {
        commandNo++;
        legalLength(path);
        Folder tmp = currentDirectory;
        List<String> paths = Tool.getDirectories(path);
        if (Tool.isAbsolutePath(path)) {
            tmp = root;
        }
        List<Folder> made = new ArrayList<>();
        if (paths.size() == 0) {
            return "/";
        }
        for (String nowpath : paths) {
            try {
                tmp = tmp.getToSonFolderWithRedirect(nowpath, root);
            } catch (NullPointerException e) {
                //this shows that there is no file or folder named nowpath under tmp
                Folder f;
                try {
                    f = new Folder(nowpath, commandNo, tmp, manager.getUser(), manager.getGroup());
                } catch (FolderException ee) {
                    //Since we check if path is legal in the beginning, this is impossible to happen
                    System.out.println("There must be something wrong");
                    recoverBuiltFolders(made);
                    throw new FolderException(path);
                }
                //We shouldn't change modifytime until path is successfully made in the end
                tmp.getSonFolder().put(nowpath, f);
                tmp = f;                       //get in this folder
                made.add(f);                //f is newly built
            } catch (IsFileException e) {
                recoverBuiltFolders(made);
                throw new FolderException(path);
            }
        }
        //Now we should setModified time
        for (Folder f : made) {
            f.getFather().setModifiedTime(commandNo);
        }
        return concatPath(tmp.getFather(), tmp.getName());
    }


    @Override
    public String removeFile(String path) throws FileSystemException {
        commandNo++;
        legalLength(path);
        Folder tmp = currentDirectory;
        if (Tool.isAbsolutePath(path)) {
            tmp = root;
        }
        List<String> paths = Tool.getDirectories(path);
        if (paths.size() == 0) {
            throw new FolderException(path);
        }
        String filename = paths.get(paths.size() - 1);
        for (int i = 0; i < paths.size() - 1; i++) {
            String p = paths.get(i);
            try {
                tmp = tmp.getToSonFolderWithRedirect(p, root);
            } catch (NullPointerException | IsFileException e) {
                throw new FolderException(path);
            }
        }
        if (tmp.getFileContains().containsKey(filename)) {
            tmp.getFileContains().remove(filename);
            List<String> newpath = new ArrayList<>(tmp.getPath());
            newpath.add(tmp.getName());
            //folderModified(commandNo, newpath);
            folderModified(commandNo, tmp);
            if (tmp.getName().equals("")) {
                return "/" + filename;
            }
            return Tool.pathToString(tmp.getPath()) + "/" + tmp.getName() + "/" + filename;
        } else {
            //throw new FolderNotFoundException(tmp.getPath() + "/" + filename);
            throw new FolderException(path);
        }
    }

    @Override
    public String removeRecursively(String path) throws FileSystemException {
        commandNo++;
        legalLength(path);
        Folder ans = currentDirectory;
        List<String> paths = Tool.getDirectories(path);
        if (paths.size() == 0) {
            throw new FolderException(path);
        }
        if (Tool.isAbsolutePath(path)) {
            currentDirectory = root;
        }
        for (int i = 0; i < paths.size() - 1; i++) {
            try {
                currentDirectory = currentDirectory.getToSonFolderWithRedirect(paths.get(i), root);
            } catch (NullPointerException | IsFileException e) {
                currentDirectory = ans;
                throw new FolderException(path);
            }
        }
        String deleteFileName = paths.get(paths.size() - 1);
        if (deleteFileName.equals("..")) {
            deleteFileName = currentDirectory.getFather().getName();
            currentDirectory = currentDirectory.getFather().getFather();
        } else if (deleteFileName.equals(".")) {
            deleteFileName = currentDirectory.getName();
            currentDirectory = currentDirectory.getFather();
        }

//        String deletePath = (currentDirectory.getPath().isEmpty()) ? "/" + deleteFileName :
//                Tool.pathToString(currentDirectory.getPath()) + "/" + currentDirectory.getName() + "/" + deleteFileName;
        String deletePath = (currentDirectory.getName().equals("")) ? "/" + deleteFileName :
                Tool.pathToString(currentDirectory.getPath()) + "/" + currentDirectory.getName() + "/" + deleteFileName;
        String ansPath = Tool.pathToString(ans.getPath()) + "/" + ans.getName();
        if (isFatherOf(deletePath, ansPath)) {
            currentDirectory = ans;
            throw new FolderException(path);
        }
        String ret = Tool.pathToString(currentDirectory.getPath()) + "/" + currentDirectory.getName() + "/" + deleteFileName;
        if (currentDirectory.getName().isEmpty()) {
            ret = "/" + deleteFileName;
        }
        //currentDirectory = currentDirectory.getToSonFolder("..");
        if (!currentDirectory.getSonFolder().containsKey(deleteFileName)) {
            currentDirectory = ans;
            throw new FolderException(path);
        }

        currentDirectory.getSonFolder().remove(deleteFileName);

        List<String> mod = new ArrayList<>(currentDirectory.getPath());
        mod.add(currentDirectory.getName());
        //folderModified(commandNo, mod);
        folderModified(commandNo, currentDirectory);
        currentDirectory = ans;
        return ret;
    }

    @Override
    public String information(String path) throws FileSystemException {
        commandNo++;
        legalLength(path);
        Folder ans = currentDirectory;
        List<String> paths = Tool.getDirectories(path);

        if (Tool.isAbsolutePath(path)) {
            currentDirectory = root;
        }
        for (int i = 0; i < paths.size() - 1; i++) {
            try {
                currentDirectory = currentDirectory.getToSonFolderWithRedirect(paths.get(i), root);
            } catch (NullPointerException | IsFileException e) {
                currentDirectory = ans;
                //throw new FolderException(Tool.pathToString(paths));
                throw new FolderException(path);
            }
        }

        if (paths.size() == 0 && currentDirectory.getName().equals("")) {
            return "root root " + root.getCreateTime() + " " + root.getModifiedTime() + " " + root.size()
                    + " " + root.count() + " /";
        }
        if (currentDirectory.getSonFolder().containsKey(paths.get(paths.size() - 1))) {
            currentDirectory = currentDirectory.getToSonFolder(paths.get(paths.size() - 1));
            String ret = currentDirectory.getUser() + " ";
            ret = ret + currentDirectory.getGroup() + " ";
            ret = ret + currentDirectory.getCreateTime() + " ";
            ret = ret.concat(currentDirectory.getModifiedTime() + " ");
            ret = ret + currentDirectory.size() + " ";
            ret = ret + currentDirectory.count() + " ";
            ret = ret.concat(Tool.pathToString(currentDirectory.getPath()) + "/" + currentDirectory.getName());
            currentDirectory = ans;
            return ret;
        } else if (currentDirectory.getFileContains().containsKey(paths.get(paths.size() - 1))) {
            String ret = currentDirectory.getFileContains().get(paths.get(paths.size() - 1)).ouputInformation();
            currentDirectory = ans;
            return ret;
        } else {
            currentDirectory = ans;
            //throw new FolderException(Tool.pathToString(paths));
            throw new FolderException(path);
        }
    }
}
