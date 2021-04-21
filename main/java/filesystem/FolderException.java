package filesystem;

import com.fileutils.specs2.models.FileSystemException;

public class FolderException extends FileSystemException {
    /**
     * 构造函数
     *
     * @param folderName 异常消息
     */
    public FolderException(String folderName) {
        super("Path " + folderName + " is invalid");
    }
}
