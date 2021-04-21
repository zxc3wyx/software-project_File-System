package filesystem;

import com.fileutils.specs2.models.FileSystem;
import com.fileutils.specs2.models.FileSystemException;
import com.fileutils.specs2.models.UserSystem;
import com.fileutils.specs2.models.UserSystemException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usersystem.MyUserSystem;

import static org.junit.Assert.*;

public class MyFileSystemTest {
    String root = "/";
    String path1;
    String path2;
    String path3;
    String path4;
    String path5;
    String path6;
    String folder1 = "folder1";
    String folder2 = "folder2";
    String folder3 = "folder3";
    String folder4 = "folder4";
    String folder5 = "folder5";
    String folder6 = "folder6";
    String file1 = "file1";
    String file2 = "file2";
    String file3 = "file3";
    String file4 = "file4";
    String file5 = "file5";
    String file6 = "file6";
    MyFileSystem fs;
    MyUserSystem us;


    private String concatPath(String... params) {
        String ret = "";
        for (String s : params) {
            if (s.equals("/")) {
                continue;
            }
            ret = ret + "/" + s;
        }
        return ret;
    }

    @Before
    public void setUp() throws Exception {
        fs = new MyFileSystem();
        us = new MyUserSystem();
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void catFile() throws FileSystemException {
        try {
            fs.touchFile("file1.txt/");
        } catch (FileSystemException e) {
            assertEquals("Path file1.txt/ is invalid", e.getMessage());
        }

        fs.fileWrite("a114514.txt", "114514");
        assertEquals("114514", fs.catFile("a114514.txt"));
        fs.fileAppend("a114513.txt", "114513");
        assertEquals("114513", fs.catFile("a114513.txt"));

        fs.makeDirectoryRecursively(concatPath(folder1, folder2));  //mkdir -p /folder1/folder2
        fs.touchFile("/.././../folder1/.///../folder1/./folder2/.//./file1.txt");
        //touch /folder1/folder2/file1.txt
        fs.fileWrite("folder1/folder2/file1.txt", "woyaocaonia");
        assertEquals("woyaocaonia",
                fs.catFile("folder1/./../folder1/folder2/./file1.txt"));
        fs.fileAppend("/./../../folder1/folder2/file1.txt", ",bushinicaowoershiwocaoni");
        assertEquals("woyaocaonia,bushinicaowoershiwocaoni",
                fs.catFile("folder1/./../folder1/folder2/./file1.txt"));
        fs.changeDirectory("folder1");       //cd folder1
        fs.fileWrite("folder2/./file1.txt", "zaizuodegeweidoushilese");
        assertEquals("zaizuodegeweidoushilese", fs.catFile("./folder2/./file1.txt"));
        fs.touchFile("/folder1/folder2/file1.txt");
        assertEquals("zaizuodegeweidoushilese", fs.catFile("/folder1/folder2/file1.txt"));
        fs.changeDirectory("/");        //cd /
        fs.touchFile("file2.txt");
        fs.fileWrite(".././file2.txt", "NINI");
        fs.fileAppend("./../file2.txt", "NB");
        assertEquals("NININB", fs.catFile(".././file2.txt"));
        fs.fileAppend("file2.txt", "@n");
        assertEquals("NININB\n", fs.catFile("file2.txt"));
        fs.fileWrite(".././file2.txt", "@N@I@NI@");
        fs.fileAppend("./../file2.txt", "nNB");
        assertEquals("@N@I@NI\nNB", fs.catFile("file2.txt"));

        fs.changeDirectory("/");
        fs.touchFile("file.txt");
        assertEquals("", fs.catFile("file.txt"));

        try {
            fs.touchFile("/folder1/ff/l.txt");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path /folder1/ff/l.txt is invalid", e.getMessage());
        }
        try {
            fs.touchFile("folder1");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path folder1 is invalid", e.getMessage());
        }

        try {
            fs.touchFile("???.txt");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path ???.txt is invalid", e.getMessage());
        }

        try {
            fs.fileWrite("???.txt", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path ???.txt is invalid", e.getMessage());
        }

        try {
            fs.fileAppend("???.txt", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path ???.txt is invalid", e.getMessage());
        }

        try {
            fs.touchFile(".");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path . is invalid", e.getMessage());
        }

        try {
            fs.touchFile("/");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }


        try {
            fs.fileWrite("folder1", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path folder1 is invalid", e.getMessage());
        }

        try {
            fs.fileWrite("/", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        try {
            fs.fileWrite("/2/folder1", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path /2/folder1 is invalid", e.getMessage());
        }

        try {
            fs.catFile("/");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        try {
            fs.catFile("folder1");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path folder1 is invalid", e.getMessage());
        }

        try {
            fs.catFile("/2/folder1");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path /2/folder1 is invalid", e.getMessage());
        }

        try {
            fs.fileAppend("/", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        try {
            fs.fileAppend("/fuck/ab.txt", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path /fuck/ab.txt is invalid", e.getMessage());
        }

        try {
            fs.fileAppend("/folder1", "114514");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path /folder1 is invalid", e.getMessage());
        }


    }

    @Test
    public void list() throws FileSystemException {
        fs.makeDirectoryRecursively("/edg/rng/we");
        fs.changeDirectory("edg");
        fs.touchFile("f1.txt");
        fs.touchFile("rng/f2.txt");
        fs.touchFile("/edg/f3.txt");
        String out1 = fs.list(".");
        String out2 = fs.list("..");
        String out3 = fs.list("/edg");
        String out4 = fs.list("rng");
        assertEquals("f1.txt f3.txt rng ", out1);
        assertEquals("edg ", out2);
        assertEquals("f1.txt f3.txt rng ", out3);
        assertEquals("f2.txt we ", out4);

        try {
            fs.list("/edg/folder1");
            fail("You miss.");
        } catch (FileSystemException e) {
            assertEquals("Path /edg/folder1 is invalid", e.getMessage());
        }
    }


    @Test
    public void mkdirANDcd() throws FileSystemException {
        try {
            fs.makeDirectory("/");
        } catch (FileSystemException e) {
            assertEquals("Path / exists", e.getMessage());
        }

        assertEquals("/folder1", fs.makeDirectory(concatPath(folder1)));      //mkdir /folder1
        assertEquals("/folder1", fs.changeDirectory(folder1));           //cd folder1
        assertEquals("/folder1/folder2", fs.makeDirectory(folder2));      //mkdir folder2
        assertEquals("/", fs.changeDirectory(root));                       //cd /
        assertEquals("/folder1/folder2/folder3",
                fs.makeDirectory(concatPath(folder1, folder2, folder3)));       //mkdir /folder1/folder2/folder3
        assertEquals("/folder1/folder2", fs.changeDirectory(concatPath(folder1, folder2)));
        //cd /folder1/folder2
        assertEquals("/folder1/folder2", fs.changeDirectory("."));   //cd .
        assertEquals("/folder1/folder2", fs.changeDirectory("././."));   // cd .
        assertEquals("/folder1", fs.changeDirectory("././../."));  //cd ..
        assertEquals("/folder1", fs.changeDirectory("/.././folder1/./folder2/./.."));
        assertEquals("/folder1", fs.changeDirectory(".././folder1/./../folder1/."));
        assertEquals("/", fs.changeDirectory("./../../../../."));    //cd /
        assertEquals("/folder4", fs.makeDirectory("folder1/./../folder4"));  // mkdir /folder4
        assertEquals("/folder4/folder5",
                fs.makeDirectory("/./.././folder4/.././folder4/../folder4/folder5"));  //mkdir /folder4/folder5

        try {
            fs.makeDirectory("///./.././//");
            fail("You miss an error!You are caibi!");
        } catch (FileSystemException e) {
            assertEquals("Path ///./.././// exists", e.getMessage());
        }

        try {
            fs.makeDirectory("/folder1");
            fail("You miss an error!You are caibi!");
        } catch (FileSystemException e) {
            assertEquals("Path /folder1 exists", e.getMessage());
        }


        try {
            fs.makeDirectory("233/folder2");
            fail("You miss an error!You are caibi!");
        } catch (FileSystemException e) {
            assertEquals("Path 233/folder2 is invalid", e.getMessage());
        }

        try {
            fs.changeDirectory("./.././114514");
            fail("You miss an error!");
        } catch (FileSystemException e) {
            assertEquals("Path ./.././114514 is invalid", e.getMessage());
        }

        try {
            fs.changeDirectory("./????/114514");
            fail("You miss an error!");
        } catch (FileSystemException e) {
            assertEquals("Path ./????/114514 is invalid", e.getMessage());
        }

        fs.changeDirectory("/");
        fs.fileWrite("a233", "233");
        try {
            fs.makeDirectory("a233");
        } catch (FileSystemException e) {
            assertEquals("Path a233 is invalid", e.getMessage());
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            s.insert(0, '/');
        }
        try {
            fs.changeDirectory(s.toString());
            fail("You miss a long path.");
        } catch (FileSystemException e) {
            assertEquals("Path " + s.toString() + " is invalid", e.getMessage());
        }

        fs.changeDirectory("/");
        fs.makeDirectory("worile");
        try {
            fs.makeDirectory("/././/worile/&*");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /././/worile/&* is invalid", e.getMessage());
        }
        try {
            fs.makeDirectory("./worile/riri/lk");
        } catch (FileSystemException e) {
            assertEquals("Path ./worile/riri/lk is invalid", e.getMessage());
        }


    }

    @Test
    public void makeDirectoryRecursively() throws FileSystemException {
        assertEquals("/folder1/folder2",
                fs.makeDirectoryRecursively(concatPath(folder1, folder2)));      //mkdir -p /folder1/folder2
        assertEquals("/folder1/folder2", fs.changeDirectory("folder1/folder2"));
        //cd folder1/folder2
        assertEquals("/folder1/folder2/folder3/folder4/folder5",
                fs.makeDirectoryRecursively("./folder3/../folder3/././folder4/folder5"));
        //mkdir -p folder3/folder4/folder5

        fs.changeDirectory("/");
        assertEquals("/", fs.makeDirectoryRecursively("/"));
        assertEquals("/folder1", fs.makeDirectoryRecursively("/folder1"));
        assertEquals("/folder1/tte", fs.makeDirectoryRecursively("/folder1/tte"));
        try {
            fs.makeDirectoryRecursively("/haha/???/wori");
            //fail("You miss it. Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /haha/???/wori is invalid", e.getMessage());
        }
        try {
            fs.changeDirectory("haha");
            // fail("I think you still make some folders when 'mkdir -p' fails.");
        } catch (FileSystemException e) {
            assertEquals("Path haha is invalid", e.getMessage());
        }

        try {
            fs.makeDirectoryRecursively("/h1/h2/h3/\n");
            // fail("I think you still make some folders when 'mkdir -p' fails.");
        } catch (FileSystemException e) {
            assertEquals("Path /h1/h2/h3/\n is invalid", e.getMessage());
        }

        fs.changeDirectory("/");
        fs.makeDirectoryRecursively("/t1/t2/t3");
        fs.changeDirectory("t1");
        fs.touchFile("c.o");
        try {
            fs.makeDirectoryRecursively("/t1/c.o");
        } catch (FileSystemException e) {
            assertEquals("Path /t1/c.o is invalid", e.getMessage());
        }

        try {
            fs.makeDirectoryRecursively("/t1/c.o/t3");
        } catch (FileSystemException e) {
            assertEquals("Path /t1/c.o/t3 is invalid", e.getMessage());
        }

        try {
            fs.makeDirectoryRecursively("/t1/t2/t3/lo#p");
        } catch (FileSystemException e) {
            assertEquals("Path /t1/t2/t3/lo#p is invalid", e.getMessage());
        }

        try {
            fs.makeDirectoryRecursively("/u1/u2/u3/u4/???/u5/u6");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /u1/u2/u3/u4/???/u5/u6 is invalid", e.getMessage());
        }
        try {
            fs.changeDirectory("/u1");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /u1 is invalid", e.getMessage());
        }

        try {
            fs.makeDirectoryRecursively("/u1/u2/u3/u4//u5/u6/???");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /u1/u2/u3/u4//u5/u6/??? is invalid", e.getMessage());
        }
        try {
            fs.changeDirectory("/u1");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /u1 is invalid", e.getMessage());
        }
    }

    @Test
    public void removeFile() throws FileSystemException {
        fs.makeDirectoryRecursively("/folder1/folder2/folder3");
        fs.touchFile("/folder1/folder2/folder3/file1.txt");
        fs.touchFile("/folder1/folder2/folder3/file2.txt");
        fs.changeDirectory("/folder1");
        assertEquals("/folder1/folder2/folder3/file1.txt",
                fs.removeFile("/../../..//.//.///folder1//folder2/./folder3/file1.txt"));
        assertEquals("/folder1/folder2/folder3/file2.txt",
                fs.removeFile("folder2//../folder2/./folder3/file2.txt"));
        fs.changeDirectory("/");
        fs.makeDirectoryRecursively("iG.Theshy/FPX.Nuguri/SN.Bin");
        fs.changeDirectory("/iG.Theshy");
        fs.touchFile("f1.txt");
        fs.touchFile("FPX.Nuguri/f2.txt");
        fs.touchFile("FPX.Nuguri/SN.Bin/f3.txt");
        String out1 = fs.list(".");
        String out2 = fs.list("/iG.Theshy/FPX.Nuguri");
        fs.removeFile("FPX.Nuguri/f2.txt");
        String out3 = fs.list("FPX.Nuguri");
        assertEquals("FPX.Nuguri f1.txt ", out1);
        assertEquals("SN.Bin f2.txt ", out2);
        assertEquals("SN.Bin ", out3);
        fs.removeFile("/iG.Theshy/f1.txt");
        String out4 = fs.list("/.././iG.Theshy");
        assertEquals("FPX.Nuguri ", out4);

        fs.changeDirectory("/");
        fs.touchFile("p.txt");
        try {
            fs.removeFile("pp.txt");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path pp.txt is invalid", e.getMessage());
        }

        try {
            fs.removeFile("/");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        fs.changeDirectory("/");
        try {
            fs.removeFile("folder1");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path folder1 is invalid", e.getMessage());
        }

        try {
            fs.removeFile("folder1/f2");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path folder1/f2 is invalid", e.getMessage());
        }

        assertEquals("/p.txt", fs.removeFile("p.txt"));

    }

    @Test
    public void removeRecursively() throws FileSystemException {
        fs.makeDirectoryRecursively("iG.Theshy/FPX.Nuguri/SN.Bin");
        fs.changeDirectory("/iG.Theshy");
        fs.touchFile("f1.txt");
        fs.touchFile("FPX.Nuguri/f2.txt");
        fs.touchFile("FPX.Nuguri/SN.Bin/f3.txt");
        String out1 = fs.list(".");
        fs.removeRecursively("FPX.Nuguri");
        String out2 = fs.list(".");
        assertEquals("FPX.Nuguri f1.txt ", out1);
        assertEquals("f1.txt ", out2);

        fs.changeDirectory("/");
        fs.makeDirectory("work");
        assertEquals("/work", fs.removeRecursively("/work"));

        fs.makeDirectoryRecursively("/q1/q2/q3");
        fs.changeDirectory("/q1/q2/q3");

        try {
            fs.removeRecursively("/");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        try {
            fs.removeRecursively("..");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path .. is invalid", e.getMessage());
        }

        try {
            fs.removeRecursively("../q3/.///.");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path ../q3/.///. is invalid", e.getMessage());
        }

        try {
            fs.removeRecursively("/q1");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path /q1 is invalid", e.getMessage());
        }

        try {
            fs.removeRecursively("/work");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path /work is invalid", e.getMessage());
        }

        try {
            fs.removeRecursively("/q1/^_^");
            fail("You miss it. Sad.");
        } catch (FolderException e) {
            assertEquals("Path /q1/^_^ is invalid", e.getMessage());
        }

        fs.changeDirectory("/");
        assertEquals("/q1/q2", fs.removeRecursively("/q1/q2"));
    }

    @Test
    public void information() throws FileSystemException {
        //create_time modify_time size count absolutePath
        String out = fs.information("..");//1
        assertEquals("root root 0 0 0 0 /", out);
        fs.makeDirectoryRecursively("d1/d2");     //2
        fs.changeDirectory("d1");            //3
        fs.touchFile("f1");               //4
        fs.fileWrite("f1", "woshiwuyuxuan, daqian");      //5
        out = fs.information("/d1");               //6
        assertEquals("root root 2 4 21 2 /d1", out);
        fs.fileWrite("/d1/f1", "piannide");         //7
        out = fs.information(".");                //8
        assertEquals("root root 2 4 8 2 /d1", out);
        out = fs.information("/");            //9
        assertEquals("root root 0 2 8 1 /", out);
        fs.makeDirectory("/d3");               //10
        fs.touchFile("/d3/f3");         //11
        fs.fileAppend("/d3/f3", "77777");    //12
        out = fs.information("/");               //13
        assertEquals("root root 0 10 13 2 /", out);

        fs.changeDirectory("/");           //14
        fs.makeDirectoryRecursively("/f1/f2/f3");         //15
        fs.changeDirectory("/f1/f2/f3");         //16
        assertEquals("root root 15 15 0 1 /f1", fs.information("../.."));       //17
        fs.touchFile("/f1/f2/f3/t");               //18
        assertEquals("root root 18 18 0 1 /f1/f2/f3/t", fs.information("t"));      //19
        assertEquals("root root 15 15 0 1 /f1", fs.information("/f1"));       //20
        fs.fileWrite("t", "114@n514");        //21
        assertEquals("root root 15 15 7 1 /f1/f2", fs.information("/f1/f2"));               //22
        assertEquals("root root 15 15 7 1 /f1", fs.information("/./f1/f2/.."));      //23
        fs.makeDirectory("ggg");     //24
        assertEquals("root root 15 15 7 1 /f1", fs.information("/f1"));          //25
        assertEquals("root root 15 24 7 2 /f1/f2/f3", fs.information("/f1/f2/f3"));
        try {
            fs.information("/114514/113");
            fail("You miss it. Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /114514/113 is invalid", e.getMessage());
        }

        try {
            fs.information("//f1///fff");
            fail("You miss it. Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path //f1///fff is invalid", e.getMessage());
        }


    }

    @Test
    public void linkHard() throws FileSystemException {
        fs.touchFile("a");
        try {
            fs.linkHard("/", "/");
            fail("Oh, Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        try {
            fs.linkHard("/f.txt", "/");
            fail("Oh, Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /f.txt is invalid", e.getMessage());
        }
        fs.touchFile("/f.txt");
        fs.makeDirectory("/home");
        try {
            fs.linkHard("/home", "/");
            fail("Sad");
        } catch (FileSystemException e) {
            assertEquals("Path /home is invalid", e.getMessage());
        }
        try {
            fs.linkHard("/a", "/b/h");
            fail("Sad");
        } catch (FileSystemException e) {
            assertEquals("Path /b/h is invalid", e.getMessage());
        }


        fs.linkHard("/f.txt", "/home/");
        fs.fileWrite("f.txt", "114514");
        assertEquals("114514", fs.catFile("/home/f.txt"));
        fs.removeFile("f.txt");
        assertEquals("114514", fs.catFile("/home/f.txt"));
        fs.fileWrite("f.txt", "114515");
        assertEquals("114514", fs.catFile("/home/f.txt"));
        try {
            fs.linkHard("f.txt", "/home");
            fail("Sad");
        } catch (FileSystemException e) {
            assertEquals("Path /home/f.txt exists", e.getMessage());
        }
    }

    @Test
    public void linkSoft() throws FileSystemException {
        try {
            fs.linkSoft("/", "/");
            fail("Oh, Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }

        try {
            fs.linkSoft("/wori", "/");
            fail("Oh, Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /wori is invalid", e.getMessage());
        }

        fs.makeDirectory("/home");
        try {
            fs.linkSoft("/", "/home");
            fail("Oh, Sad");
        } catch (FileSystemException e) {
            assertEquals("Path /home is invalid", e.getMessage());
        }
        try {
            fs.linkSoft("/home", "/");
            fail("Oh, Sad");
        } catch (FileSystemException e) {
            assertEquals("Path //home exists", e.getMessage());
        }


        fs.linkSoft("/home//", "/.././/shome");
        assertEquals("/home", fs.changeDirectory("shome"));
        assertEquals("/home", fs.changeDirectory(".././shome/../shome/"));
        assertEquals("/", fs.changeDirectory("../shome//..//"));
        fs.makeDirectory("buaa");
        try {
            fs.linkSoft("/home", "shome");
            fail("Sad");
        } catch (FileSystemException e) {
            assertEquals("Path shome is invalid", e.getMessage());
        }

        try {
            fs.linkSoft("shome", "/");
            fail("Sad");
        } catch (FileSystemException e) {
            assertEquals("Path //home exists", e.getMessage());
        }

        fs.linkSoft("shome", "/buaa//");
        try {
            fs.makeDirectory("/buaa/home");
            fail("JUST DO IT.");
        } catch (FileSystemException e) {
            assertEquals("Path /buaa/home exists", e.getMessage());
        }

        try {
            fs.makeDirectory("shome/home//home");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path shome/home//home is invalid", e.getMessage());
        }
        fs.linkSoft("/buaa", "/sbuaa");
        fs.linkSoft("/buaa", "/home/qqq");
        fs.linkSoft("/home", "/buaa/www");
        assertEquals("/home", fs.changeDirectory(
                "/buaa//.//www/qqq/www/qqq/www/qqq/www/qqq/www/qqq/www/qqq/www/qqq/www"));
        assertEquals("/home", fs.changeDirectory("/home///.///qqq/www/qqq/www/./qqq/www"));
        fs.linkSoft("/buaa", "/home/www");
        assertEquals("/buaa", fs.changeDirectory("/home//www//./www//www//www///www///"));
        assertEquals("/home", fs.changeDirectory("/home//www/./www//www//www//www///www/"));
        assertEquals("/", fs.changeDirectory("www/../..//"));
        assertEquals("/home/www", fs.removeFile("/home/www"));
        fs.removeRecursively("/home");
        try {
            fs.changeDirectory("/buaa/www");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /home is invalid", e.getMessage());
        }
        fs.makeDirectory("/home");
        assertEquals("/home", fs.changeDirectory("/buaa/www"));
        fs.touchFile("/home/f1.txt");
        try {
            fs.linkSoft("/home/f1.txt/", "/");
        } catch (FileSystemException e) {
            assertEquals("Path /home/f1.txt/ is invalid", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.linkSoft("home/f1.txt", "f1.txt");
        fs.linkSoft("f1.txt", "buaa/f1.txt");
        fs.fileWrite("f1.txt", "114514");
        assertEquals("114514", fs.catFile("/buaa/f1.txt"));
        assertEquals("114514", fs.catFile("home/f1.txt"));
        fs.removeFile("f1.txt");
        assertEquals("114514", fs.catFile("/buaa/f1.txt"));
        assertEquals("/home/f1.txt", fs.removeFile("/buaa/www/../buaa/www/f1.txt"));

        fs.changeDirectory("/");
        fs.fileWrite("file1.txt", "114514");
        assertEquals("/folder1/folder2", fs.makeDirectoryRecursively("folder1/folder2"));
        fs.linkSoft("/folder1/folder2/..", "slink1");
        assertEquals("/folder1", fs.readLink("/slink1"));
        assertEquals("/folder1", fs.changeDirectory("//slink1/."));
        fs.changeDirectory("/");
        try {
            fs.linkHard("slink1", "hlink1");
        } catch (FileSystemException e) {
            assertEquals("Path slink1 is invalid", e.getMessage());
        }
        fs.linkSoft("file1.txt", "slink2");
        fs.linkHard("slink2", "hlink1");
        fs.linkHard("hlink1", "hlink2");
        fs.linkSoft("hlink2", "slink3");

        try{
            fs.linkSoft("slink2","slink3");
        }catch (FileSystemException e){
            assertEquals("Path slink3 exists",e.getMessage());
        }


        assertEquals("114514", fs.catFile("slink2"));
        assertEquals("114514", fs.catFile("hlink1"));
        assertEquals("114514", fs.catFile("hlink2"));
        assertEquals("114514", fs.catFile("slink3"));

        try{
            fs.linkHard("slink2","slink3");
        }catch (FileSystemException e){
            assertEquals("Path slink3 exists",e.getMessage());
        }

        try{
            fs.linkHard("slink3","slink3");
        }catch (FileSystemException e){
            assertEquals("Path slink3 is invalid",e.getMessage());
        }



        try{
            fs.linkSoft("slink2","slink3");
        }catch (FileSystemException e){
            assertEquals("Path slink3 exists",e.getMessage());
        }

        assertEquals("114514", fs.catFile("slink3"));

        fs.changeDirectory("/");
        fs.makeDirectoryRecursively("/a/b/c/d/e");
        fs.linkSoft("a/b/c/d/e","slink4");
        fs.linkHard("slink2","slink4");
        assertEquals("114514",fs.catFile("a/b/c/d/e/file1.txt"));
        fs.removeFile("slink4/file1.txt");
        fs.linkSoft("slink2","slink4");
        assertEquals("114514",fs.catFile("slink4/file1.txt"));
        try{
            fs.linkSoft("slink2","/file1.txt");
        }catch (FileSystemException e){
            assertEquals("Path /file1.txt exists",e.getMessage());
        }

    }

    @Test
    public void readLink() throws FileSystemException {
        fs.makeDirectory("po");
        fs.makeDirectory("NB");
        fs.linkSoft("po", "./link1");
        fs.makeDirectory("NB/wori");
        fs.changeDirectory("NB");
        fs.linkSoft("wori", "/link2");
        assertEquals("/po", fs.readLink("/link1"));
        assertEquals("/NB/wori", fs.readLink("/link2"));
        try {
            fs.readLink("/NB");
            fail("Sad");
        } catch (FileSystemException e) {
            assertEquals("Path /NB is invalid", e.getMessage());
        }
        try{
            fs.readLink("/a/b");
        }catch (FileSystemException e){
            assertEquals("Path /a/b is invalid",e.getMessage());
        }
        try{
            fs.readLink("/");
        }catch (FileSystemException e){
            assertEquals("Path / is invalid",e.getMessage());
        }

    }

    @Test
    public void move() throws FileSystemException {
        fs.makeDirectory("qqq");
        fs.makeDirectory("www");
        try {
            fs.move("sss", "www");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path sss is invalid", e.getMessage());
        }
        try {
            fs.move("/", "/");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }
        fs.move("qqq", "www");
        assertEquals("/www/qqq", fs.changeDirectory("/www/qqq"));
        fs.changeDirectory("/");
        fs.makeDirectory("qqq");
        fs.makeDirectory("/www/qqq/eee");
        try {
            fs.move("qqq", "www");
        } catch (FileSystemException e) {
            assertEquals("Path www/qqq exists", e.getMessage());
        }
        fs.move("qqq", "qqqq");
        assertEquals("/qqqq", fs.changeDirectory("qqqq"));
        fs.fileWrite("ui", "114514");
        fs.fileWrite("rr", "233");
        fs.move("ui", "iu");
        assertEquals("114514", fs.catFile("iu"));
        try {
            fs.catFile("ui");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path ui is invalid", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.move("/qqqq", "/www");
        assertEquals("114514", fs.catFile("/www/qqqq/iu"));
        try {
            fs.move("/./www", "/www/qqqq");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /www/qqqq is invalid", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.makeDirectory("zzz");
        fs.makeDirectory("xxx");
        fs.changeDirectory("zzz");
        try {
            fs.move("/zzz", "/xxx");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /zzz is invalid", e.getMessage());
        }


        fs.changeDirectory("/");
        fs.makeDirectory("test");
        fs.changeDirectory("test");
        fs.makeDirectory("folder1");
        fs.makeDirectory("folder2");
        fs.fileWrite("file1.txt", "caonima");
        fs.fileWrite("file2.txt", "wo yao cao ni a");
        fs.move("folder1", "folder2/folder1");
        assertEquals("/test/folder2/folder1", fs.changeDirectory("folder2/folder1"));
        fs.changeDirectory("/test");
        fs.move("file1.txt", "/test/folder2/file1.txt");
        assertEquals("caonima", fs.catFile("/test/folder2/file1.txt"));
        fs.changeDirectory("/test");
        fs.fileWrite("file1.txt", "114514");
        fs.move("file2.txt", "folder2");
        assertEquals("wo yao cao ni a", fs.catFile("/test/folder2/file2.txt"));
        fs.move("file1.txt", "folder2");
        assertEquals("114514", fs.catFile("/test/folder2/file1.txt"));
        fs.fileWrite("folder1", "1");
        try {
            fs.move("folder1", "folder2");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path folder2/folder1 exists", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.removeRecursively("test");
        fs.makeDirectory("test");
        fs.changeDirectory("test");
        fs.fileWrite("f1", "wo yao cao ni a");
        fs.fileWrite("f2", "bu shi ni cao wo er shi wo cao ni");
        fs.move("f1", "f2");
        assertEquals("wo yao cao ni a", fs.catFile("f1"));
        try {
            fs.catFile("f2");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path f2 is invalid", e.getMessage());
        }
        fs.makeDirectory("fd1");
        fs.makeDirectory("fd1/fd2");
        fs.makeDirectory("fd2");
        fs.makeDirectory("fd3");
        fs.move("fd1", "fd3");
        assertEquals("/test/fd3/fd1", fs.changeDirectory("fd3/fd1"));
        fs.changeDirectory("/test/");
        fs.move("fd2", "fd3/fd1");
        assertEquals("/test/fd3/fd1/fd2", fs.changeDirectory("fd3/fd1/fd2"));
        fs.changeDirectory("/test");
        fs.makeDirectory("fd1");
        try {
            fs.move("fd1", "fd3");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path fd3/fd1 exists", e.getMessage());
        }
        fs.touchFile("/test/fd3/fd1/fd2/fd1");
        try {
            fs.move("fd1", "fd3/fd1/fd2");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path fd3/fd1/fd2/fd1 exists", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.removeRecursively("/test");
        fs.makeDirectory("/test");
        fs.makeDirectory("/test/fd1");
        fs.touchFile("/test/f1");
        try {
            fs.move("/test/fd1", "/test/f1");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /test/f1 exists", e.getMessage());
        }
    }

    @Test
    public void copy() throws FileSystemException {
        try {
            fs.copy("/", "/");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path / is invalid", e.getMessage());
        }
        fs.makeDirectory("qqq");
        fs.makeDirectory("www");
        fs.copy("qqq", "www");
        assertEquals("/www/qqq", fs.changeDirectory("www/qqq"));
        fs.changeDirectory("/");
        fs.fileWrite("qqq/f.txt", "114514");
        fs.copy("qqq", "www");
        assertEquals("114514", fs.catFile("www/qqq/f.txt"));


        fs.changeDirectory("/");
        fs.makeDirectory("test");
        fs.changeDirectory("test");
        fs.makeDirectory("folder1");
        fs.makeDirectory("folder2");
        fs.fileWrite("file1.txt", "caonima");
        fs.fileWrite("file2.txt", "wo yao cao ni a");
        fs.copy("folder1", "folder2/folder1");
        fs.removeRecursively("folder1");
        assertEquals("/test/folder2/folder1", fs.changeDirectory("folder2/folder1"));
        fs.changeDirectory("/test");
        fs.copy("file1.txt", "/test/folder2/file1.txt");
        fs.removeFile("file1.txt");
        assertEquals("caonima", fs.catFile("/test/folder2/file1.txt"));
        fs.changeDirectory("/test");
        fs.fileWrite("file1.txt", "114514");
        fs.copy("file2.txt", "folder2");
        fs.removeFile("file2.txt");
        assertEquals("wo yao cao ni a", fs.catFile("/test/folder2/file2.txt"));
        fs.copy("file1.txt", "folder2");
        fs.removeFile("file1.txt");
        assertEquals("114514", fs.catFile("/test/folder2/file1.txt"));
        fs.fileWrite("folder1", "1");
        try {
            fs.copy("folder1", "folder2");
            fs.removeFile("folder1");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path folder2/folder1 exists", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.removeRecursively("test");
        fs.makeDirectory("test");
        fs.changeDirectory("test");
        fs.fileWrite("f1", "wo yao cao ni a");
        fs.fileWrite("f2", "bu shi ni cao wo er shi wo cao ni");
        fs.copy("f1", "f2");
        //fs.removeFile("f1");
        assertEquals("wo yao cao ni a", fs.catFile("f2"));
        assertEquals("wo yao cao ni a", fs.catFile("f1"));

        fs.makeDirectory("fd1");
        fs.makeDirectory("fd1/fd2");
        fs.fileWrite("fd1/fd1_f1.txt","114514");
        fs.makeDirectory("fd2");
        fs.makeDirectory("fd3");
        fs.copy("fd1", "fd3");
        fs.removeRecursively("fd1");
        assertEquals("/test/fd3/fd1", fs.changeDirectory("fd3/fd1"));
        fs.changeDirectory("/test/");
        fs.copy("fd2", "fd3/fd1");
        fs.removeRecursively("fd2");
        assertEquals("/test/fd3/fd1/fd2", fs.changeDirectory("fd3/fd1/fd2"));
        fs.changeDirectory("/test");
        fs.makeDirectory("fd1");
        try {
            fs.copy("fd1", "fd3");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path fd3/fd1 exists", e.getMessage());
        }
        fs.touchFile("/test/fd3/fd1/fd2/fd1");
        try {
            fs.copy("fd1", "fd3/fd1/fd2");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path fd3/fd1/fd2/fd1 exists", e.getMessage());
        }
        fs.changeDirectory("/");
        fs.removeRecursively("/test");
        fs.makeDirectory("/test");
        fs.makeDirectory("/test/fd1");
        fs.touchFile("/test/f1");
        try {
            fs.copy("/test/fd1", "/test/f1");
            fail("Sad.");
        } catch (FileSystemException e) {
            assertEquals("Path /test/f1 exists", e.getMessage());
        }

    }

    @Test
    public void otherTests() throws FileSystemException {
        assertEquals(0,fs.getCommandNo());
        fs.setCommandNo(0);
        assertEquals("/",fs.getCurrentDirectory());
        String slinkp = "/a/b/c/d/e/f";
        String slink = "/slink";
        fs.makeDirectoryRecursively(slinkp + "/g/h");
        fs.linkSoft(slinkp, slink);
        fs.removeRecursively("/a/b/c/d");
        try{
            fs.changeDirectory(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.touchFile(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.catFile(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.list(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.fileWrite(slink,"cao");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.fileAppend(slink,"RNM");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.makeDirectory(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.makeDirectoryRecursively(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.removeRecursively(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.removeFile(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.information(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.linkSoft(slink,"/ss");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.linkHard(slink+"/e","/ss");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.readLink(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.move(slink+"/e","/");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.copy(slink+"/e","//");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        assertEquals(slinkp,fs.readLink(slink));
        assertEquals("root root 2 2 0 1 /slink",fs.information(slink));
        assertEquals("/slink",fs.removeFile(slink));
        fs.removeRecursively("/a");
        fs.makeDirectoryRecursively(slinkp + "/g/h");
        fs.touchFile(slinkp+"/f1");
        fs.linkSoft(slinkp+"/f1", slink);
        String p="/a/b/c/d/e/f";
        try{
            fs.changeDirectory(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slink+" is invalid",e.getMessage());
        }
        try{
            fs.touchFile(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.catFile(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.list(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slink+" is invalid",e.getMessage());
        }
        try{
            fs.fileWrite(slink,"cao");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.fileAppend(slink,"RNM");
        }catch (FileSystemException e){
            assertEquals("Path "+slinkp+" is invalid",e.getMessage());
        }
        try{
            fs.makeDirectory(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slink+" is invalid",e.getMessage());
        }
        try{
            fs.makeDirectoryRecursively(slink);
        }catch (FileSystemException e){
            assertEquals("Path "+slink+" is invalid",e.getMessage());
        }
        try{
            fs.removeRecursively(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }
        try{
            fs.removeFile(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }
        try{
            fs.information(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }
        try{
            fs.linkSoft(slink,"/ss");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+" is invalid",e.getMessage());
        }
        try{
            fs.linkHard(slink+"/e","/ss");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }
        try{
            fs.readLink(slink+"/e");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }
        try{
            fs.move(slink+"/e","/");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }
        try{
            fs.copy(slink+"/e","//");
        }catch (FileSystemException e){
            assertEquals("Path "+slink+"/e"+" is invalid",e.getMessage());
        }

        try{
            fs.touchFile("/++");
        }catch (FileSystemException e){
            assertEquals("Path /++ is invalid",e.getMessage());
        }

        fs.makeDirectoryRecursively("/a/b/c/d/e");
        assertEquals("/a/b/c",fs.removeRecursively("a/b/c/./."));

        try{
            fs.makeDirectory("././p_-+");
        }catch (FileSystemException e){
            assertEquals("Path ././p_-+ is invalid",e.getMessage());
        }

        fs.makeDirectoryRecursively("/a/b/c/d/e/f");
        fs.touchFile("/a/b/c/d/f1.txt");
        fs.touchFile("/a/b/c/f2.txt");
        fs.linkSoft("/a/b","/a/slk1");
        fs.linkSoft("/a/b/c/f2.txt","/a/slk2");
        try{
            fs.linkSoft("/a/slk1","/a/slk2");
        }catch (FileSystemException e){
            assertEquals("Path /a/slk2 exists",e.getMessage());
        }
        fs.removeRecursively("/a");
        fs.makeDirectoryRecursively("/a/b/c/d/e/f");
        fs.makeDirectory("/a/a_1");
        fs.move("/a/a_1/.","/a/b/c/d/e/..");
        fs.makeDirectoryRecursively("/a/a_1/a__1/a___1");
        fs.copy("/a/a_1/.","/a/b/c/d/po");
        fs.copy("/a/a_1/.","/a/b/c/d/e/..");
        fs.makeDirectory("/a/b/c/d/g");
        fs.copy("/a/a_1/.","/a/b/c/d/g");

        fs.makeDirectory("/mk");
        fs.touchFile("/mk/f.txt");
        try{
            fs.makeDirectory("/mk/f.txt/r");
            fail("Sad.");
        }catch (FileSystemException e){
            assertEquals("Path /mk/f.txt/r is invalid",e.getMessage());
        }

        fs.linkSoft("/a/b/c","/mk/link");
        fs.removeRecursively("/a/b/c");
        try{
            fs.makeDirectory("/mk/link/kk");
        }catch (FileSystemException e){
            assertEquals("Path /a/b/c is invalid",e.getMessage());
        }



    }

    @Test
    public void interestingTests() throws FileSystemException, UserSystemException {
        fs.makeDirectoryRecursively("a/b/c/d/e/f/g");      //1
        fs.fileWrite("a/b/f1","114@n514");     //2
        us.addUser("yyh");                //3
        us.changeUser("yyh");              //4
        fs.fileWrite("a/b/c/f1","114514");        //5
        us.exitUser();                  //6
        fs.copy("a/b/f1","a/b/c/f1");    //7
        assertEquals("yyh yyh 5 7 7 1 /a/b/c/f1",fs.information("a/b/c/f1"));
        assertEquals("c f1 ",fs.list("/a/b"));
    }
}