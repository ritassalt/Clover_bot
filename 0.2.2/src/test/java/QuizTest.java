package test.java;

import main.Quiz;
import main.Question;
import java.util.*;


import org.junit.*;
import static org.junit.Assert.*;


public class QuizTest
{	
	private Quiz quiz = null;
	@Before
    public void init() { quiz = new Quiz(12); }
    @After
    public void tearDown() { quiz = null; }
    
	@Test
	public void testMakeQuiz1() throws Exception {
		Question[] questions = quiz.makeQuiz();
		assertTrue(questions.length == 12);
	}
	
	@Test
	public void testMakeQuiz2() throws Exception {
		Question[] questions = quiz.makeQuiz();
		for (Question q: questions)	{
			assertNotNull(q);
		}
	}
	
	@Test
	public void testScore() throws Exception {
		quiz.checkAnswer(quiz.getAnswer());
		assertTrue(quiz.getScore() == 10);
	}
	
	@Test
	public void testShuffle() throws Exception {
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
	public void testNextQuestion() throws Exception {
		String question = quiz.getCurrQuest();
		quiz.checkAnswer("a");
		assertTrue(question != quiz.getCurrQuest());
	}
	
	@Test
	public void testIsEnd() throws Exception {
		for (int i = 0; i < 12; i++) {
			quiz.checkAnswer("a");
		}
		assertTrue(quiz.isEnd());
	}
	
	@Test
	public void TestCheckAnswer() throws Exception {
		String answer = quiz.getAnswer();
		assertTrue(quiz.checkAnswer(answer));
	}
}
