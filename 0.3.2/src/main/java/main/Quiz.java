package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz
{
	private int score;
	private int combo;
	private String userID;
	protected static List<Question> questions = makeQuestions();
	protected int[] quiz;
	protected int currNumbQuest;
	protected int length;
	protected int earnedScore = 10;
	protected boolean end = false;
	private boolean extraLife = false;

	public Quiz(String userID, int len) {
		this.userID = userID;
		length = len;
		quiz = makeQuiz();
		currNumbQuest = 0;
		combo = 0;
	}
	
	private static List<Question> makeQuestions() {
		List<Question> temp = new ArrayList<Question>();
		int k = 0;
		k++;
		for (String line : readFile("questions.txt")) {
		    temp.add(new Question(line, k));
		}
		return temp;
	}
	
	public int[] makeQuiz() {
		Random rnd = new Random();
		quiz = new int[length];
		for (int i = 0; i < length; i++) {
			while (quiz[i] == 0) {
				int number = rnd.nextInt(questions.size());
				boolean exist = false;
				for (int num : quiz) {
					if (num == number) {
						exist = true;
					}
				}
				if (!exist) {
					quiz[i] = number;
				}
			}	
		}
		return quiz;
	}
	
	public boolean checkAnswer(String answ) {
		boolean right = questions.get(quiz[currNumbQuest]).checkAnswer(answ);
		if (right) {
			combo += 1;
			score += earnedScore * combo;
		} else if (!extraLife) {
			combo = 0;
		}
		extraLife = false;
		nextQuestion();
		if (currNumbQuest >= length) {
			end = true;
		}
		return right;		
	}

	public static List<String> readFile(String name) {
		List<String> res = new ArrayList<>();
		String s;
		try {
			InputStream iStream = DataBase.class.getClassLoader().getResourceAsStream(name);
			BufferedReader reader = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));
			s = reader.readLine();
			while (s != null) {
			    res.add(s);
			    s = reader.readLine();
            }
            return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void nextQuestion() { currNumbQuest += 1; }
	
	public String getCurrQuest() {
	    return Integer.toString(currNumbQuest + 1) + ". " + questions.get(quiz[currNumbQuest]).getCurrQuest();
	}

	public int getScore() { return score; }

    public String getUserID() { return userID; }

	public String getAnswer() { return questions.get(quiz[currNumbQuest]).getAnswer();	}
	
	public boolean isEnd() { return end; }

	public void activateExtraLife() { extraLife = true; }

	public boolean isExtraLifeActive() { return extraLife; }
}