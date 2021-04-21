package filesystem;

import usersystem.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class File implements Cloneable, Serializable {
    private String name;

    private Data data;

    //private List<String> path;
    private Folder father;

    private int type;        //0:normal file  1:soft link file 2:hard link file

    public void setFather(Folder father) {
        this.father = father;
    }


    public void setName(String name) {
        this.name = name;
    }


    public File(String name, Folder father, int create_time, String user, String group) throws FileNameIllegalException, InputPathLengthExceed4096 {

        String regex = "^([a-zA-Z]|\\.|_)([a-zA-Z]|\\.|_|[0-9]){0,255}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        //this.path = Tool.getDirectories(path);
        this.father = father;
        if (!m.find()) {
            throw new FileNameIllegalException(Tool.pathToString(this.getPath()) + "/" + name);
        }
        this.name = name;
        //this.size = 0;
        //this.content = "";
        this.data = new Data(create_time, user, group);
        //this.create_time = create_time;
        //this.modify_time = create_time;
        type = 0;
    }

    public File(String name, Folder father, int create_time, Data d) throws FileNameIllegalException {
        //used for hard link

        String regex = "^([a-zA-Z]|\\.|_)([a-zA-Z]|\\.|_|[0-9]){0,255}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        ///this.path = Tool.getDirectories(path);
        this.father = father;
        if (!m.find()) {
            throw new FileNameIllegalException(Tool.pathToString(getPath()) + "/" + name);
        }
        this.name = name;
        //this.size = 0;
        //this.content = "";
        this.data = d;
        d.addPointnum();
        //d.setModify_time(create_time);
        //this.create_time = create_time;
        //this.modify_time = create_time;
        type = 2;
    }

    public File(String name, Folder father, int create_time, String targetpath, String user, String group) throws FileNameIllegalException {
        //used for soft link
        String regex = "^([a-zA-Z]|\\.|_)([a-zA-Z]|\\.|_|[0-9]){0,255}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        //this.path = Tool.getDirectories(path);
        this.father = father;
        if (!m.find()) {
            throw new FileNameIllegalException(Tool.pathToString(getPath()) + "/" + name);
        }
        this.name = name;
        this.data = new Data(targetpath, create_time, user, group);
        type = 1;
    }

    // private int countSize() {

//        int length = 0;
//        boolean flag = false;
//        for (int i = 0; i < content.length(); i++) {
//            if (content.charAt(i) != '@') {
//                if (flag && content.charAt(i) == 'n') {
//                    flag = false;
//                    continue;
//                } else {
//                    flag = false;
//                    length++;
//                }
//            } else {
//                flag = true;
//                length++;
//            }
//        }
//        return length;
    //  }

    public void writeFile(String content, int modify_time) {
        this.data.writeData(content);
        //this.size = countSize(content);
        this.data.setModify_time(modify_time);
    }

    public void appendFile(String content, int modify_time) {
        this.data.appendData(content);
        //this.content = this.content + content;
        //this.size = countSize(this.content);
        //this.modify_time = modify_time;
        this.data.setModify_time(modify_time);
    }

    public String outputFile() {
        String s = data.getContent();
        if (data.getSize() != 0) {
            String special = "@n";
            Pattern p = Pattern.compile(special);
            Matcher m = p.matcher(s);
            if (m.find()) {
                s = m.replaceFirst("\n");
                //System.out.println(m.replaceFirst("\n"));
            }// else {
            // return s;
            //System.out.println(content);
            // }
            //return content;
        }
        return s;
    }

    public String ouputInformation() {
        //System.out.println(Tool.pathToString(path) + "/" + name + ": " + create_time + " " + modify_time + " " + size);
        return data.getUser() + " " + data.getGroup() + " " + data.getCreate_time() + " " + data.getModify_time()
                + " " + getSize() + " 1 " + Tool.pathToString(getPath()) + "/" + name;
    }

    public int getSize() {
        if (type == 1) {
            return 0;
        }
        return data.getSize();
    }

    public String getName() {
        return name;
    }

    public void setModify_time(int time) {
        data.setModify_time(time);
    }

    public int getType() {
        return type;
    }

    public Data getData() {
        return data;
    }

    @Override
    protected File clone() throws CloneNotSupportedException {
        return (File) super.clone();
    }

    public File deepClone() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(this);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (File) objectInputStream.readObject();
    }

//    public Folder getFather() {
//        return father;
//    }

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

    public void setCreateTime(int time) {
        data.setCreate_time(time);
    }

//    public String getCreateUser() {
//        return data.getUser();
//    }
//
//    public void setCreateUser(String s) {
//        data.setUser(s);
//    }
//
//    public String getCreateGroup() {
//        return data.getGroup();
//    }
//
//    public void setCreateGroup(String s) {
//        data.setGroup(s);
//    }


}
