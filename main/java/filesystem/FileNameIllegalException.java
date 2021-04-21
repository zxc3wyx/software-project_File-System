package filesystem;

import com.fileutils.specs2.models.FileSystemException;

public class FileNameIllegalException extends FileSystemException {
    public FileNameIllegalException(String path) {
        super("Path " + path + " is invalid");
    }
}
