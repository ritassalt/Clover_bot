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
	private int score;
	private static List<Question> questions = makeQuestions();
	private Question[] quiz;
	private int currNumbQuest;
	private boolean isEnd = false;

	public Quiz() {
		quiz = makeQuiz();
		currNumbQuest = 0;
	}
	
	public String getCurrQuest() {
		return Integer.toString(currNumbQuest + 1) + ". " + quiz[currNumbQuest].getCurrQuest();
	}
	
	public Question[] makeQuiz() {
		Random rnd = new Random();
		quiz = new Question[12];
		for (int i = 0; i < quiz.length; i++) {
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
		boolean right = answ.toLowerCase().equals(quiz[currNumbQuest].getAnswer().toLowerCase());
		if (right) {
			score += 10;
		}
		nextQuestion();
		if (currNumbQuest >= 12) {
			isEnd = true;
		}
		return right;		
	}
	
	private void nextQuestion() {
		currNumbQuest += 1;
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
	
	private static List<Question> makeQuestions() {
		if (questions != null) {
			return questions;
		}
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
}
