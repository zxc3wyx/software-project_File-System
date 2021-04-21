package usersystem;

import com.fileutils.specs2.models.UserSystemException;

public class GroupExistException extends UserSystemException {
    public GroupExistException(String message) {
        super(String.format("Group %s exists", message));
    }
}
