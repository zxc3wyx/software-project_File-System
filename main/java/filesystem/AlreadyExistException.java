package filesystem;

import com.fileutils.specs2.models.FileSystemException;

public class AlreadyExistException extends FileSystemException {
    public AlreadyExistException(String path) {
        super("Path " + path + " exists");
    }
}
