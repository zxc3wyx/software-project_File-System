package filesystem;

import com.fileutils.specs2.models.FileSystemException;

public class InputPathLengthExceed4096 extends FileSystemException {
    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public InputPathLengthExceed4096(String message) {
        super("Path " + message + " is invalid");
    }
}
