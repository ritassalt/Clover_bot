package main;

import java.util.Random;

public final class Question {

	private String question;
	private String[] answers;
	private String rightAnswer;
	private String rightAnswerNumber;

	public Question(String line) {
		String[] questWithAnswers = line.split("#");
		question = questWithAnswers[0];
		rightAnswer = questWithAnswers[2];
		answers = shuffleAnswers(questWithAnswers[1]);
		for (int i = 0; i < answers.length; i++) {
			if (answers[i].equals(rightAnswer)) {
				rightAnswerNumber = Integer.toString(i + 1);
				break;
			}
		}
	}

	public static String[] shuffleAnswers(String answer) {
		String[] answers = answer.split(",");
		Random rnd = new Random();
		for (int i = 1; i < answers.length; i++) {
			int j = rnd.nextInt(i);
			String temp = answers[i];
			answers[i] = answers[j];
			answers[j] = temp;
		}
		return answers;
	}

	public String getAnswer() {
		return rightAnswer;
	}


	public String getCurrQuest() {
		StringBuilder questWithAnswers = new StringBuilder();
		questWithAnswers.append(question);
		for (int i = 0; i < 4; i++) {
			questWithAnswers.append("\n" + answers[i]);
		}
		return questWithAnswers.toString();
	}

	public boolean checkAnswer(String answer) {
		return rightAnswerNumber.equals(answer) ||
				answer.toLowerCase().equals(rightAnswer.toLowerCase()) ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Question q = (Question) o;
		return this.getAnswer().equals(q.getAnswer());
	}
	//TODO: getHashCode
}