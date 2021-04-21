package usersystem;

import com.fileutils.specs2.models.FileSystemException;
import filesystem.MyFileSystem;

public class Manager {
    private MyFileSystem myFileSystem;
    private MyUserSystem myUserSystem;

    public void setMyFileSystem(MyFileSystem myFileSystem) {
        this.myFileSystem = myFileSystem;
    }

    public void setMyUserSystem(MyUserSystem myUserSystem) {
        this.myUserSystem = myUserSystem;
    }

    public String getCurrentDirectory() {
        return myFileSystem.getCurrentDirectory();
    }

    public void changeDirectory(String directory) throws FileSystemException {
        myFileSystem.setCommandNo(myFileSystem.getCommandNo() - 1);
        myFileSystem.changeDirectory(directory);

    }

    public String getUser() {
        return myUserSystem.getCurrentUserName();
    }

    public String getGroup() {
        return myUserSystem.getCurrentGroup();
    }

    public void addCommandNo() {
        myFileSystem.setCommandNo(myFileSystem.getCommandNo() + 1);
    }
}
