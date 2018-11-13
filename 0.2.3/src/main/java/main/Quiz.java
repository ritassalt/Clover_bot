package main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz
{
	protected int score;
	protected static List<Question> questions = makeQuestions();
	protected Question[] quiz;
	protected int currNumbQuest;
	protected int length;
	protected int earnedScore = 10;
	protected boolean isEnd = false;

	public Quiz(int len) {
		length = len;
		quiz = makeQuiz();
		currNumbQuest = 0;
	}
	
	private static List<Question> makeQuestions() {
		List<Question> temp = new ArrayList<Question>();
		try {
			for (String line : Files.readAllLines(Paths.get("src\\questions.txt"), StandardCharsets.UTF_8)) {
				temp.add(new Question(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public Question[] makeQuiz() {
		Random rnd = new Random();
		quiz = new Question[length];
		for (int i = 0; i < length; i++) {
			while (quiz[i] == null) {				
				int number = rnd.nextInt(questions.size());
				boolean exist = false;
				for (Question question : quiz) {
					Question anotherQuestion = questions.get(number);
					if (anotherQuestion.equals(question)) {
						exist = true;
					}
				}
				if (!exist) {
					quiz[i] = questions.get(number);
				}
			}	
		}
		return quiz;
	}
	
	public boolean checkAnswer(String answ) {
		boolean right = quiz[currNumbQuest].checkAnswer(answ);
		if (right) {
			score += earnedScore;
		}
		nextQuestion();
		if (currNumbQuest >= length) {
			isEnd = true;
		}
		return right;		
	}
	
	private void nextQuestion() {
		currNumbQuest += 1;
	}
	
	public String getCurrQuest() {
		return Integer.toString(currNumbQuest + 1) + ". " + quiz[currNumbQuest].getCurrQuest();
	}
	
	public int getScore() {
		return score;
	}
	
	public String getAnswer() {
		return quiz[currNumbQuest].getAnswer();
	}
	
	public boolean isEnd() {
		return isEnd;
	}
}