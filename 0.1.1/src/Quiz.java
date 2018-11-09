import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class Quiz
{	
	private String currentQuestion;
	private String[] currentAnswers;
	private String rightAnswer;
	private int score;
	private static List<String> Questions;
	private String[] quiz;
	private int currNumbQuest;
	private boolean isEnd = false;

	public Quiz() 
	{
		try {
			Questions = Files.readAllLines(Paths.get("C:\\Users\\vital\\Desktop\\Java\\Clover_bot\\questions.txt"), StandardCharsets.UTF_8);
			Questions.removeIf((s) -> s == "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		quiz = MakeQuiz();
	}
	
//	public String[] getCurrQuest() - для работы с кнопками в дальнейшем
//	{
//		String[] questWithAnswers = new String[5];
//		questWithAnswers[0] = currentQuestion;
//		for (int i = 1; i < questWithAnswers.length; i++)
//			questWithAnswers[i] = currentAnswers[i-1];
//		return questWithAnswers;
//	}
	
	public String getCurrQuest()
	{
		StringBuilder questWithAnswers = new StringBuilder();
		questWithAnswers.append(currentQuestion);
		for (int i = 0; i < 4; i++)
			questWithAnswers.append("\n" + currentAnswers[i]);
		return questWithAnswers.toString();
	}
	
	public void NextQuestion(int i)
	{
		String[] questWithAnswers = quiz[i].split("#"); // вопрос#ответ1,ответ2,ответ3,ответ4#верный ответ
		currentQuestion = Integer.toString(i + 1) + ". " + questWithAnswers[0];
		currentAnswers = ShuffleAnswers(questWithAnswers[1]);
		rightAnswer = questWithAnswers[2];
	}
	
	public String[] MakeQuiz()
	{
		Random rnd = new Random();
		quiz = new String[12];
		for (int i = 0; i < quiz.length; i++)
			while (quiz[i] == null) 
			{				
				int number = rnd.nextInt(Questions.size());
				boolean exist = false;
				for (String question : quiz)
					if (question == Questions.get(number))
						exist = true;
				if (!exist)
					quiz[i] = Questions.get(number);
			}
		NextQuestion(0);
		return quiz;
	}
	
	public String[] ShuffleAnswers(String answer)
	{
		String[] answers = answer.split(",");
		Random rnd = new Random();
        for (int i = 1; i < answers.length; i++)
        {            
            int j = rnd.nextInt(i);
            String temp = answers[i];
            answers[i] = answers[j];
            answers[j] = temp;
        }
        return answers;
	}
	
	public boolean CheckAnswer(String answ)
	{
		boolean right = answ.toLowerCase().equals(rightAnswer.toLowerCase());
		if (right)
			score += 10;
		currNumbQuest += 1;
		if (currNumbQuest < 12)
			NextQuestion(currNumbQuest);
		else
			isEnd = true;
		return right;		
	}
	
	public int getScore()
	{
		return score;
	}
	
	public String getAnswer()
	{
		return rightAnswer;
	}
	
	public boolean isEnd()
	{
		return isEnd;
	}
}
