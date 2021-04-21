package filesystem;

import com.fileutils.specs2.models.UserSystem;
import org.junit.*;
import usersystem.MyUserSystem;

import static org.junit.Assert.assertEquals;

public class FileTest {

    private File file1;
    private File file2;
    private File file3;
//    public FileTest() throws FileNameIllegalException {
//        file1=new File("file1.txt","/user/app",5);
//        file2=new File("file2.txt","/",4);
//        file3=new File("file3.txt","///user//app",3);
//    }

    @BeforeClass
    public static void beginTest() {

        System.out.println("FileTest begin.");
    }

    @AfterClass
    public static void endTest() {
        System.out.println("FileTest end.");
    }

    @Before
    public void setUp() throws Exception {
//        file1 = new File("file1.txt", "/user/app", 5);
//        file2 = new File("file2.txt", "/", 4);
//        file3 = new File("file3.txt", "///user//app", 3);
        file1 = new File("file1.txt", null, 5,"root","root");
        file2 = new File("file2.txt", null, 4,"root","root");
        file3 = new File("file3.txt", null, 3,"root","root");
        MyFileSystem fs=new MyFileSystem();
        UserSystem us=new MyUserSystem();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = Exception.class)
    public void excep() throws FileNameIllegalException, InputPathLengthExceed4096 {
        File file4 = new File("2f.txt", null, 114514,"root","root");
    }


    @Test
    public void getSize() {
        file1.writeFile("2+3@n", 7);
        file2.writeFile("@@n", 8);
        file3.writeFile("114514", 9);
        assertEquals(4, file1.getSize());
        assertEquals(2, file2.getSize());
        assertEquals(6, file3.getSize());
    }

    @Test
    public void appendFile() {
        file1.appendFile("144", 5);
        assertEquals("144", file1.outputFile());
        file1.writeFile("2+3@", 7);
        file2.writeFile("@@n", 8);
        file3.writeFile("114514", 9);
        file1.appendFile("n", 11);
        file2.appendFile("nn", 18);
        file3.appendFile("114514", 20);
        //assertEquals("root root 5 11 4 /user/app/file1.txt", file1.ouputInformation());
        //assertEquals("root root 4 18 4 /file2.txt", file2.ouputInformation());
        //assertEquals("root root 3 20 12 /user/app/file3.txt", file3.ouputInformation());
    }

    @Test
    public void outputFile() {
        assertEquals("", file1.outputFile());
        file1.writeFile("2+3@n1", 7);
        file2.writeFile("@@n", 8);
        file3.writeFile("114514", 9);
        assertEquals("2+3\n1", file1.outputFile());
        assertEquals("@\n", file2.outputFile());
        assertEquals("114514", file3.outputFile());
    }

    @Test
    public void ouputInformation() {
        //assertEquals("/user/app/file1.txt: 5 5 0", file1.ouputInformation());
        //assertEquals("/file2.txt: 4 4 0", file2.ouputInformation());
        //assertEquals("/user/app/file3.txt: 3 3 0", file3.ouputInformation());
    }
}