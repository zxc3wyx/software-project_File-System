package usersystem;

import com.fileutils.specs2.models.FileSystemException;
import com.fileutils.specs2.models.UserSystem;
import com.fileutils.specs2.models.UserSystemException;
import filesystem.MyFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyUserSystemTest {
    MyFileSystem fs;
    MyUserSystem us;

    private String exists(String s,boolean group){
        if(group){
            return "Group "+s+" exists";
        }
        return "User "+s+" exists";
    }

    private String invalid(String s,boolean group){
        if(group){
            return "Group "+s+" is invalid";
        }
        return "User "+s+" is invalid";
    }

    private String notPermitted(){
        return "Operation is not permitted";
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
    public void addUser() throws UserSystemException {
        us.addUser("dog");
        try{
            us.addUser("dog");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("User dog exists",e.getMessage());
        }
        try{
            us.addUser("???");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("User ??? is invalid",e.getMessage());
        }
        try{
            us.addUser("root");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("Operation is not permitted",e.getMessage());
        }
        us.changeUser("dog");
        try{
            us.addUser("RNM");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.exitUser();
        us.deleteUser("dog");
        us.addUser("dog");
        us.changeUser("dog");
        assertEquals("dog",us.getCurrentGroup());
    }

    @Test
    public void deleteUser() throws UserSystemException {
        us.addUser("dog");
        us.addUser("cat");
        try{
            us.deleteUser("root");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.changeUser("dog");
        try{
            us.deleteUser("cat");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.exitUser();
        try{
            us.deleteUser("person");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(invalid("person",false),e.getMessage());
        }
        us.deleteUser("cat");
        try{
            us.deleteUser("cat");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(invalid("cat",false),e.getMessage());
        }

    }

    @Test
    public void addGroup() throws UserSystemException {
        try{
            us.addGroup("root");
            fail("Sad");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.addGroup("dog");
        try{
            us.addGroup("dog");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(exists("dog",true),e.getMessage());
        }
        try{
            us.addGroup("???");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("Group ??? is invalid",e.getMessage());
        }
        try{
            us.addGroup("root");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("Operation is not permitted",e.getMessage());
        }
    }

    @Test
    public void deleteGroup() throws UserSystemException {

        try{
            us.deleteGroup("root");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.addGroup("dog");
        us.addGroup("cat");
        us.addUser("dog");
        us.changeUser("dog");
        try{
            us.deleteGroup("cat");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.exitUser();
        try{
            us.deleteGroup("person");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(invalid("person",true),e.getMessage());
        }
        us.deleteGroup("cat");
        try{
            us.deleteGroup("cat");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(invalid("cat",true),e.getMessage());
        }
        us.addUser("pdd");
        try{
            us.deleteGroup("pdd");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(invalid("pdd",true),e.getMessage());
        }
        us.addUserToGroup("dog","pdd");
        us.deleteUser("dog");
        us.deleteGroup("dog");
        us.addGroup("dog");
    }

    @Test
    public void addUserToGroup() throws UserSystemException {
        us.addUser("cat");
        try{
            us.addUserToGroup("root","root");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        try{
            us.addUserToGroup("cat","dog");
        }catch (UserSystemException e){
            assertEquals(invalid("dog",false),e.getMessage());
        }
        try{
            us.addUserToGroup("dog","cat");
        }catch (UserSystemException e){
            assertEquals(invalid("dog",true),e.getMessage());
        }
    }

    @Test
    public void changeUser() throws UserSystemException {

        try{
            us.changeUser("root");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("Operation is not permitted",e.getMessage());
        }
        try{
            us.changeUser("dog");
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals("User dog is invalid",e.getMessage());
        }

        assertEquals("root",us.getCurrentGroup());
        us.addUser("dog");
        us.changeUser("dog");
        assertEquals("dog",us.getCurrentGroup());
    }

    @Test
    public void exitUser() throws UserSystemException, FileSystemException {

        try{
            us.exitUser();
            fail("Sad.");
        }catch (UserSystemException e){
            assertEquals(notPermitted(),e.getMessage());
        }
        us.addUser("e");
        us.changeUser("e");
        assertEquals("e",us.queryUser());
        us.exitUser();
        assertEquals("root",us.queryUser());

        fs.makeDirectoryRecursively("/a/b/c/d/e");
        fs.changeDirectory("/a/b/c");
        us.changeUser("e");
        fs.changeDirectory("/a");
        fs.removeRecursively("/a/b");
        us.exitUser();
        assertEquals("/",fs.changeDirectory("."));

        fs.makeDirectoryRecursively("/a/b/c/d/e");
        fs.changeDirectory("/a/b/c");
        us.changeUser("e");
        fs.changeDirectory("/a");
        us.exitUser();
        assertEquals("/a/b/c",fs.changeDirectory("."));
    }






}