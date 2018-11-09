package test.java;
import org.junit.*;
import static org.junit.Assert.*;
import main.Duel;

public class DuelTest {
    private Duel duel = null;
    @Before
    public void init() { duel = new Duel(12, "123", "321"); }
    @After
    public void tearDown() { duel = null; }

    @Test
    public void testReady() {
        assertFalse(duel.getIsReady());
    }

    @Test
    public void testReadyAfterAnswer() {
        duel.checkAnswer("a", "123");
        duel.checkAnswer("a", "321");
        assertTrue(duel.getIsReady());
    }

    @Test
    public void testGetUserID() {
        assertTrue(duel.getOpponent("123").equals("321"));
        assertTrue(duel.getOpponent("321").equals("123"));
    }

    @Test
    public void testCorrectAnswer() {
        duel.checkAnswer(duel.getAnswer(), "321");
        assertTrue(duel.getScore("321") == 10);
        assertTrue(duel.getScore("123") == 0);
    }

    @Test
    public void testIsEnd() {
        for (int i = 0; i < 12; i++) {
            duel.checkAnswer("qwerty", "123");
            duel.checkAnswer("qwerty", "321");
            duel.reset();
        }
        assertTrue(duel.isEnd());
    }
}
