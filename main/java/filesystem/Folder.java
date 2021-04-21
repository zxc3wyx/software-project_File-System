package filesystem;

import com.fileutils.specs2.models.FileSystemException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Folder implements Serializable, Cloneable {
    private String name;
    private HashMap<String, Folder> sonFolder = new HashMap<>();
    private HashMap<String, File> fileContains = new HashMap<>();
    private Folder father;
    private int createTime;
    private int modifiedTime = 0;
    private String user;
    private String group;

    public Folder(String name, int createTime, Folder father, String user, String group) throws FolderException {
        String regex = "^[a-zA-Z._][a-zA-Z._0-9]{0,255}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if (!m.find()) {
            throw new FolderException("You should handle this.");
        }
        this.name = name;
        this.createTime = createTime;
        this.modifiedTime = createTime;
        this.father = father;
        sonFolder.put(".", this);
        if (father == null) {
            this.father = this;
            sonFolder.put("..", this);
        } else {
            sonFolder.put("..", father);
        }
        this.user = user;
        this.group = group;
    }

    public Folder() {         //only for root
        this.name = "";
        this.createTime = 0;
        this.modifiedTime = 0;
        sonFolder.put(".", this);
        this.father = this;
        sonFolder.put("..", this);
        sonFolder.put("", this);
        user = "root";
        group = "root";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Folder> getSonFolder() {
        return sonFolder;
    }

    public HashMap<String, File> getFileContains() {
        return fileContains;
    }

    public int getCreateTime() {
        return createTime;
    }

    public List<String> getPath() {
        ArrayList<String> paths = new ArrayList<>();
        Folder tmp = father;
        while (!(tmp.getName().equals("") && tmp.getFather().getName().equals(""))) {
            paths.add(tmp.getName());
            tmp = tmp.getFather();
        }
        Collections.reverse(paths);
        return paths;
    }

    public Folder getFather() {
        return father;
    }

    public void setFather(Folder father) {
        this.father = father;
    }

    public int getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(int modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public int size() {
        int cnt = 0;
        for (File file : fileContains.values()) {
            cnt += file.getSize();
        }
//        for (Folder folder : sonFolder.values()) {
//            if (folder.getName().equals(name) || folder.getName().equals(father.getName())) {
//                continue;
//            }
//            cnt += folder.size();
//        }
        for (String fdname : sonFolder.keySet()) {
            if (fdname.equals("..") || fdname.equals(".") || fdname.equals("")) {
                continue;
            }
            cnt += sonFolder.get(fdname).size();
        }

        return cnt;
    }

    public Folder getToSonFolder(String name) throws NullPointerException {
        if (name.equals(".")) {
            return this;
        } else if (name.equals("..")) {
            if (father == null) {
                return this;
            }
            return father;
        }
        if (!sonFolder.containsKey(name)) {
            throw new NullPointerException();
        }
        return sonFolder.get(name);
    }

    public Folder getToSonFolderWithRedirect(String name, Folder root)
            throws NullPointerException, FileSystemException, IsFileException {
        if (name.equals(".")) {
            return this;
        } else if (name.equals("..")) {
            return father;
        }
        Folder son1 = sonFolder.get(name);
        File son2 = fileContains.get(name);
        if (son1 != null) {
            return son1;
        }
        if (son2 != null && son2.getType() != 1) {
            //throw new NullPointerException();
            throw new IsFileException(son2, this);
        } else if (son2 != null && son2.getType() == 1) {
            String pathString = son2.getData().getContent();
            Folder tmp;
            if (pathString.charAt(0) == '/') {
                tmp = root;
            } else {
                tmp = this;
            }
            List<String> paths = Tool.getDirectories(pathString);
            Folder cur = tmp;
            try {
                for (int i = 0; i < paths.size() - 1; i++) {
                    tmp = tmp.getToSonFolder(paths.get(i));
                }
            } catch (NullPointerException e) {
                throw new SoftLinkException(Tool.simplifyPath(Tool.pathToString(cur.getPath()) + pathString));
            }
            String fname = paths.get(paths.size() - 1);
            if (tmp.getFileContains().containsKey(fname)) {
                throw new IsFileException(tmp.getFileContains().get(fname), tmp);
            } else if (tmp.getSonFolder().containsKey(fname)) {
                tmp = tmp.getToSonFolder(fname);
            } else {
                throw new SoftLinkException(Tool.simplifyPath(Tool.pathToString(cur.getPath()) + pathString));
            }
            return tmp;
        } else {
            throw new NullPointerException();
        }
    }

    public Folder deepClone() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(this);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (Folder) objectInputStream.readObject();
    }

    @Override
    protected Folder clone() throws CloneNotSupportedException {
        return (Folder) super.clone();
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int count() {
        int cnt = 0;
        for (File file : fileContains.values()) {
            if (file.getType() != 2) {
                cnt++;
            }
        }
        for (String folder : sonFolder.keySet()) {
            if (folder.equals(".") || folder.equals("..") || folder.equals("")) {
                continue;
            }
            cnt += 1;
        }
        return cnt;
    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
