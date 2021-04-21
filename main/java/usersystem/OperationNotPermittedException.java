package usersystem;

import com.fileutils.specs2.models.UserSystemException;

public class OperationNotPermittedException extends UserSystemException {

    public OperationNotPermittedException(String message) {
        super("Operation is not permitted");
    }
}
