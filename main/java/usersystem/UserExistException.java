package usersystem;

import com.fileutils.specs2.models.UserSystemException;

public class UserExistException extends UserSystemException {
    public UserExistException(String message) {
        super(String.format("User %s exists", message));
    }
}
