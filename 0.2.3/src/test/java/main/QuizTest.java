package main;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;


public class QuizTest {
	private Quiz quiz = null;
	@BeforeEach
    public void init() { quiz = new Quiz(12); }
    @AfterEach
    public void tearDown() { quiz = null; }

	@Test
	public void testMakeQuiz1() {
		Question[] questions = quiz.makeQuiz();
        assertEquals(12, questions.length);
	}
	
	@Test
	public void testMakeQuiz2() {
		Question[] questions = quiz.makeQuiz();
		for (Question q: questions)	{
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
	public void TestCheckAnswer() {
		String answer = quiz.getAnswer();
		assertTrue(quiz.checkAnswer(answer));
	}
}
