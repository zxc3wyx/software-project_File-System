import filesystem.MyFileSystem;
import usersystem.MyUserSystem;
import com.fileutils.specs2.AppRunner;

public class Main {
    public static void main(String[] args) throws Exception {
        AppRunner runner = AppRunner.newInstance(MyFileSystem.class, MyUserSystem.class);
        runner.run(args);
    }
}