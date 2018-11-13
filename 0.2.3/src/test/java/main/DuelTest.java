package main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class DuelTest {
    private Duel duel = new Duel(12,"123","321");
    @BeforeEach
    public void init() { duel = new Duel(12, "123", "321"); }
    @AfterEach
    public void tearDown() { duel = null; }

    @Test
    public void testReady() {
        assertFalse(duel.isReady());
    }

    @Test
    public void testReadyAfterAnswer() {
        duel.checkAnswer("a", "123");
        duel.checkAnswer("a", "321");
        assertTrue(duel.isReady());
    }

    @Test
    public void testGetOpponentID() {
        assertEquals("321", duel.getOpponent("123"));
        assertEquals("123", duel.getOpponent("321"));
    }

    @Test
    public void testGetYourself() {
        duel = new Duel(12,"123","123");
        assertEquals("123", duel.getOpponent("123"));
    }

    @Test
    public void testGetIsReady() {
        assertFalse(duel.isReady("123"));
        assertFalse(duel.isReady("321"));
    }

    @Test
    public void testCorrectAnswer() {
        duel.checkAnswer(duel.getAnswer(), "321");
        assertEquals(10, duel.getScore("321"));
        assertEquals(0, duel.getScore("123"));
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

    @Test
    public void testAccept() {
        assertFalse(duel.isAccepted());
        duel.accept();
        assertTrue(duel.isAccepted());
    }

    @Test
    public void testGetInviter() {
        assertEquals(duel.getInviter(), "123");
    }
}
