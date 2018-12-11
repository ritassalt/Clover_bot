package main;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DataBaseTest {

    private DataBase base = new DataBase("src\\test\\resources\\testData.txt");

    @Test
    public void testBaseContains() {
        assertTrue(base.contains("371010181"));
    }

    @Test
    public void testBaseFind() {
        assertNull(base.find("abcde"));
        assertEquals("371010181", base.find("Astrom123"));
    }

    @Test
    public void testGetUsername() {
        assertEquals("Astrom123", base.getUsername("371010181"));
    }

    @Test
    public void testBaseScores() throws IOException {
        String content1 = Files.lines(Paths.get("src\\test\\resources\\testData.txt")).toString();
        int startScore = base.getScore("371010181");
        base.addScores("371010181", 1000);
        assertEquals(1000, base.getScore("371010181") - startScore);
        base.Save();
        String content2 = Files.lines(Paths.get("src\\test\\resources\\testData.txt")).toString();
        assertNotEquals(content1, content2);
    }

    @Test
    public void testSaveNewUser() throws IOException {
        String content1 = Files.lines(Paths.get("src\\test\\resources\\testData.txt")).toString();
        base.addNewUser(new String[]{"123", "abc"});
        assertTrue(base.contains("123"));
        base.addNewUser(new String[]{"321"});
        assertTrue(base.contains("321"));
        String content2 = Files.lines(Paths.get("src\\test\\resources\\testData.txt")).toString();
        assertNotEquals(content1, content2);
    }

    @Test
    public void testGetUserData() {
        UserDataObject user = base.getUserData("123");
        assertEquals("123", user.getUserID());
    }
}
