import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import java.util.*;


public class QuizTest
{	
	private Quiz quiz = null;
	@Before
    public void init() { quiz = new Quiz(); }
    @After
    public void tearDown() { quiz = null; }
    
	@Test
	public void TestMakeQuiz1() throws Exception 
	{
		String[] questions = quiz.MakeQuiz();
		assertTrue(questions.length == 12);
	}
	
	@Test
	public void TestMakeQuiz2() throws Exception 
	{
		String[] questions = quiz.MakeQuiz();
		for (String q: questions)
		{
			assertTrue(q != "");
			assertNotNull(q);
		}
	}
	
	@Test
	public void TestScore() throws Exception
	{
		quiz.CheckAnswer(quiz.getAnswer());
		assertTrue(quiz.getScore() == 10);
	}
	
	@Test
	public void TestShuffle() throws Exception
	{
		String answer = "A,B,C,D";
		String[] answers = answer.split(",");
		int equal = 0;
		for (int i=0; i < 100; i++)
		{
			String[] shuffledAnswers = quiz.ShuffleAnswers(answer);
			if (Arrays.equals(shuffledAnswers, answers))
				equal++;
		}
		assertTrue(equal <= 25);
	}
	
	@Test
	public void TestNextQuestion() throws Exception
	{
		String question = quiz.getCurrQuest();
		quiz.NextQuestion(1);
		assertTrue(question != quiz.getCurrQuest());
	}
	
	@Test
	public void TestIsEnd() throws Exception
	{
		for (int i = 0; i < 12; i++)
		{
			quiz.CheckAnswer("answer");
		}
		assertTrue(quiz.isEnd());
	}
	
	@Test
	public void TestCheckAnswer() throws Exception
	{
		String answer = quiz.getAnswer();
		assertTrue(quiz.CheckAnswer(answer));
	}
}
