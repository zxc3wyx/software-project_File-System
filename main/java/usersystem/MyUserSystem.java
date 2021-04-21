package usersystem;

import com.fileutils.specs2.models.FileSystemException;
import com.fileutils.specs2.models.UserSystem;
import com.fileutils.specs2.models.UserSystemException;
import filesystem.MyFileSystem;

import java.util.HashMap;

public class MyUserSystem implements UserSystem {
    private String currentUserName = "root";
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Group> groups = new HashMap<>();
    private String lastSuDirectory = "/";
    private Manager manager = MyFileSystem.getManager();


    {
        manager.setMyUserSystem(this);
        try {
            users.put("root", new User("root"));
            groups.put("root", new Group("root"));
        } catch (UserSystemException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void addUser(String s) throws UserSystemException {
        manager.addCommandNo();
        if (s.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (!currentUserName.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (users.containsKey(s)) {
            throw new UserExistException(s);
        }
        User user = new User(s);
        if (groups.containsKey(s)) {
            users.put(s, user);
            user.setMainGroup(s);
        } else {
            users.put(s, user);
            user.setMainGroup(s);
            groups.put(s, new Group(s));
            groups.get(s).getUsers().add(s);
        }
    }

    @Override
    public void deleteUser(String s) throws UserSystemException {
        manager.addCommandNo();
        if (s.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (!currentUserName.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (!users.containsKey(s)) {
            throw new UserInvalidException(s);
        }
        for (Group group : groups.values()) {
            group.getUsers().remove(s);
        }
        if (groups.get(users.get(s).getMainGroup()).getUsers().isEmpty()) {
            groups.remove(users.get(s).getMainGroup());
        }
        users.remove(s);
    }

    @Override
    public void addGroup(String s) throws UserSystemException {
        manager.addCommandNo();
        if (!currentUserName.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (s.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (groups.containsKey(s)) {
            throw new GroupExistException(s);
        }
        groups.put(s, new Group(s));
    }

    @Override
    public void deleteGroup(String s) throws UserSystemException {
        manager.addCommandNo();
        if (!currentUserName.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (s.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (!groups.containsKey(s)) {
            throw new GroupInvalidException(s);
        }
        for (User user : users.values()) {
            if (user.getMainGroup().equals(s)) {
                throw new GroupInvalidException(s);
            }
        }
        groups.remove(s);
    }

    @Override
    public void addUserToGroup(String s, String s1) throws UserSystemException {
        manager.addCommandNo();
        if (s.equals("root") || s1.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (!groups.containsKey(s)) {
            throw new GroupInvalidException(s);
        }
        if (!users.containsKey(s1) || groups.get(s).getUsers().contains(s1)) {
            throw new UserInvalidException(s1);
        }
        groups.get(s).getUsers().add(s1);
    }

    @Override
    public String changeUser(String s) throws UserSystemException {
        manager.addCommandNo();
        if (!currentUserName.equals("root") || s.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        if (!users.containsKey(s)) {
            throw new UserInvalidException(s);
        }
        currentUserName = s;
        lastSuDirectory = manager.getCurrentDirectory();
        return s;
    }

    @Override
    public String exitUser() throws UserSystemException{
        manager.addCommandNo();
        if (currentUserName.equals("root")) {
            throw new OperationNotPermittedException("");
        }
        currentUserName = "root";
        try {
            manager.changeDirectory(lastSuDirectory);
        } catch (FileSystemException e) {
            try {
                manager.changeDirectory("/");
            } catch (FileSystemException ex) {
                ex.printStackTrace();
            }
        }
        return "root";
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentGroup() {
        return users.get(currentUserName).getMainGroup();
    }

    @Override
    public String queryUser() throws UserSystemException {
        manager.addCommandNo();
        return currentUserName;
    }
}
