package usersystem;

import com.fileutils.specs2.models.UserSystemException;

public class UserInvalidException extends UserSystemException {
    public UserInvalidException(String message) {
        super(String.format("User %s is invalid", message));
    }
}
