package filesystem;


import com.fileutils.specs2.models.FileSystemException;

public class SoftLinkException extends FileSystemException {
    public SoftLinkException(String path) {
        super("Path " + path + " is invalid");
    }
}
