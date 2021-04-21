package filesystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;


public class FolderTest {
    Folder f1;
    Folder f2;
    Folder f3;

    @Before
    public void setUp() throws Exception {
        List<String> path1 = new ArrayList<>();
        List<String> path2 = new ArrayList<>();
        List<String> path3 = new ArrayList<>();
        path1.add("root");
        path1.add("user");
        path2.add("root");
        path2.add("p2");
        path2.add("p3");
        path3.add("root");
        f3 = new Folder("f3", 16, null,"root","root");
        f1 = new Folder("f1", 12, f3,"root","root");
        f2 = new Folder("f2", 15, f3,"root","root");

        System.out.println("FolderTest begin.");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("FolderTest end.");
    }

    @Test
    public void size() throws FileNameIllegalException, InputPathLengthExceed4096 {
//        File file1 = new File("file1.txt", "/root/user", 25);
//        File file2 = new File("file2.txt", "/root/user", 25);
        File file1 = new File("file1.txt", null, 25,"root","root");
        File file2 = new File("file2.txt", null, 25,"root","root");
        assertEquals(0, f1.size());
        f1.getFileContains().put(file1.getName(), file1);
        f1.getFileContains().put(file2.getName(), file2);
        assertEquals(0, f1.size());
        file1.appendFile("114514", 30);
        assertEquals(6, f1.size());
        file2.appendFile("114514@n", 30);
        assertEquals(13, f1.size());
        f3.getSonFolder().put(f1.getName(), f1);
        f3.getSonFolder().put(f2.getName(), f2);
        assertEquals(13, f3.size());
    }

    @Test
    public void deepClone() throws IOException, ClassNotFoundException, FolderException {
        List<String> paths = new ArrayList<>();
        paths.add("p1");
        paths.add("p2");
        Folder root=new Folder();
        Folder f1 = new Folder("f1", 1, root,"root","root");
        Folder f2 = f1.deepClone();
        //f1.getPath().remove(0);
        System.out.println(f1.getPath().size());
        System.out.println(f2.getPath().size());
    }
}