package main;

import java.util.HashMap;
import java.util.Map;

public class Duel extends Quiz {

	private Map<String, Boolean> isReady;
	private Map<String, Integer> scores;
	
	public Duel(int len, String userID1, String userID2) {
		super(len);
		scores = new HashMap<String, Integer>();
		scores.put(userID1, 0);
		scores.put(userID2, 0);
		isReady = new HashMap<String, Boolean>();
		isReady.put(userID1, false);
		isReady.put(userID2, false);
	}

	public boolean checkAnswer(String answ, String userID) {
		boolean right = quiz[currNumbQuest].checkAnswer(answ);
		if (right && !isReady.get(userID)) {
			scores.put(userID, scores.get(userID) + earnedScore);
		}
		isReady.put(userID, true);
		nextQuestion();
		if (currNumbQuest >= length && getIsReady()) {
			isEnd = true;
		}
		return right;		
	}
	
	private void nextQuestion() {
		for (boolean ready: isReady.values()) {
			if (!ready) {
				return;
			}
		}
		currNumbQuest += 1;
	}
	
	public boolean getIsReady() {
		for (boolean ready: isReady.values()) {
			if (!ready) {
				return false;
			}
		}
		return true;
	}

	public boolean getIsReady(String userID) {
		return isReady.get(userID);
	}
	
	public void reset() {
		for (String userID: isReady.keySet()) {
			isReady.put(userID, false);
		}
	}
	
	public String getOpponent(String userID1) {
		for (String userID: isReady.keySet()) {
			if (!userID.equals(userID1)) {
				return userID;
			}
		}
		return userID1;
	}
	
	public int getScore(String userID) {
		return scores.get(userID);
	}
}