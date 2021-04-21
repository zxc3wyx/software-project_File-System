package usersystem;

import java.util.HashSet;

public class Group {
    private String name;
    private HashSet<String> users = new HashSet<>();

    public Group(String name) throws GroupInvalidException {
        if (!name.matches("[a-zA-Z.\\-_]{1,128}")) {
            throw new GroupInvalidException(name);
        }
        this.name = name;
    }

    public HashSet<String> getUsers() {
        return users;
    }


}
