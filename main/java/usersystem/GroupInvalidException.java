package usersystem;

import com.fileutils.specs2.models.UserSystemException;

public class GroupInvalidException extends UserSystemException {
    public GroupInvalidException(String message) {
        super(String.format("Group %s is invalid", message));
    }
}
