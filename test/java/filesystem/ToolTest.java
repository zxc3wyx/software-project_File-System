package filesystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ToolTest {
    private Tool tool;

    @Before
    public void setUp() throws Exception {
        System.out.println("开始测试...");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("测试结束...");
    }

    @Test
    public void isAbsolutePath() {
        boolean out1 = Tool.isAbsolutePath("/HansBug/is/sb");
        boolean out2 = Tool.isAbsolutePath("HansBug/niuB");
        boolean out3 = Tool.isAbsolutePath("///Shu/xue/jian/mo/nmsl");
        assertTrue(out1);
        assertFalse(out2);
        assertTrue(out3);
    }

    @Test
    public void pathToString() {
        String[] src1 = {"lu", "ben", "wei", "niu", "bi"};
        List<String> list = new ArrayList<>(Arrays.asList(src1));
        String out1 = Tool.pathToString(list);
        String[] src2 = {"wo", "shi", "wyx", "daQian"};
        list = new ArrayList<>(Arrays.asList(src2));
        String out2 = Tool.pathToString(list);
        list.clear();
        String out3 = Tool.pathToString(list);
        assertEquals("/lu/ben/wei/niu/bi", out1);
        assertEquals("/wo/shi/wyx/daQian", out2);
        assertEquals("", out3);
    }

    @Test
    public void simplifyPath() {
        String[] input = {"/.././../wdnmd/ri/ss/./..", "/w/q/././../../../.././", "/////", "/w/pp/", "/f/f"};
        String[] ans = {"/wdnmd/ri", "/", "/", "/w/pp", "/f/f"};
        for (int i = 0; i <= 4; i++) {
            assertEquals(ans[i],Tool.simplifyPath(input[i]));
        }
    }
}