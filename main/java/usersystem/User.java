package usersystem;

public class User {
    private String name;
    private String mainGroup;


    public User(String name) throws UserInvalidException {
        if (!name.matches("[a-zA-Z.\\-_]{1,128}")) {
            throw new UserInvalidException(name);
        }
        this.name = name;
        mainGroup = name;
    }



    public String getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(String mainGroup) {
        this.mainGroup = mainGroup;
    }
}
