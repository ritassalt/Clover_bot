package test.java;
import org.junit.*;
import static org.junit.Assert.*;
import main.DataBase;

public class DataBaseTest {

    private DataBase base = new DataBase();

    @Test
    public void testBaseContains() {
        assertTrue(base.contains("371010181"));
    }

    @Test
    public void testBaseFind() {
        assertTrue(base.find("Astrom123").equals("371010181"));
    }

    @Test
    public void testBaseScores() {
        int startScore = base.getScore("371010181");
        base.addScores("371010181", 1000);
        assertTrue(base.getScore("371010181") - startScore == 1000);
    }
}
