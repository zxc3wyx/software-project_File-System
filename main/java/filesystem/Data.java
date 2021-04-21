package filesystem;

import java.io.Serializable;

public class Data implements Cloneable, Serializable {
    private int pointnum;
    private String content;
    private int create_time;
    private int modify_time;
    private String user;

//    public void setUser(String user) {
//        this.user = user;
//    }
//
//    public void setGroup(String group) {
//        this.group = group;
//    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    private String group;

    public Data(int create_time, String user, String group) {
        this.create_time = create_time;
        this.modify_time = create_time;
        content = "";
        pointnum = 1;
        this.user = user;
        this.group = group;
    }

    public Data(String path,int create_time, String user, String group) {
        this.create_time = create_time;
        this.modify_time = create_time;
        this.content = path;
        this.user = user;
        this.group = group;
        pointnum = 1;
    }


    public void addPointnum() {
        pointnum++;
    }

    public String getContent() {
        return content;
    }

    public void writeData(String content) {
        this.content = content;
    }

    public void appendData(String content) {
        this.content = this.content + content;
    }

    public int getSize() {
        int length = 0;
        boolean flag = false;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) != '@') {
                if (flag && content.charAt(i) == 'n') {
                    flag = false;
                    continue;
                } else {
                    flag = false;
                    length++;
                }
            } else {
                flag = true;
                length++;
            }
        }
        return length;
    }

    public int getCreate_time() {
        return create_time;
    }

    public int getModify_time() {
        return modify_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public void setModify_time(int modify_time) {
        this.modify_time = modify_time;
    }

    @Override
    protected Data clone() throws CloneNotSupportedException {
        return (Data) super.clone();
    }
}
