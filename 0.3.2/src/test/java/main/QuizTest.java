package main;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


public class QuizTest {
	private Quiz quiz = null;
	@BeforeEach
    public void init() { quiz = new Quiz("123", 12); }
    @AfterEach
    public void tearDown() { quiz = null; }

	@Test
	public void testMakeQuiz1() {
		int[] questions = quiz.makeQuiz();
        assertEquals(12, questions.length);
	}
	
	@Test
	public void testMakeQuiz2() {
		int[] questions = quiz.makeQuiz();
		for (int q: questions)	{
			assertNotNull(q);
		}
	}
	
	@Test
	public void testScore() {
		quiz.checkAnswer(quiz.getAnswer());
        assertEquals(10, quiz.getScore());
	}
	
	@Test
	public void testShuffle() {
		String answer = "A,B,C,D";
		String[] answers = answer.split(",");
		int equal = 0;
		for (int i = 0; i < 100; i++) {
			String[] shuffledAnswers = Question.shuffleAnswers(answer);
			if (Arrays.equals(shuffledAnswers, answers)) {
				equal++;
			}
		}
		assertTrue(equal <= 25);
	}
	
	@Test
	public void testNextQuestion() {
		String question = quiz.getCurrQuest();
		quiz.checkAnswer("a");
        assertNotEquals(question, quiz.getCurrQuest());
	}
	
	@Test
	public void testIsEnd() {
		for (int i = 0; i < 12; i++) {
			quiz.checkAnswer("a");
		}
		assertTrue(quiz.isEnd());
	}
	
	@Test
	public void testCheckAnswer() {
		String answer = quiz.getAnswer();
		assertTrue(quiz.checkAnswer(answer));
	}

	@Test
    public void testUserID() {
	    assertEquals("123", quiz.getUserID());
    }

    @Test
    public void testExtraLifeActivation() {
	    quiz.activateExtraLife();
	    assertTrue(quiz.isExtraLifeActive());
    }

    @Test
    public void testQuestionNumber() {
	    Question question1 = new Question("quest#a,b,c,d#a", 1);
        Question question2 = new Question("quest#a,b,c,d#a", 1);
        assertEquals(1, question1.getNumber());
        assertEquals(question1, question2);
        assertEquals(question1, question1);
        assertNotEquals(question1, null);
    }
}
