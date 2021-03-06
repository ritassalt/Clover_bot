package main;

import java.util.HashMap;
import java.util.Map;

public class Duel extends Quiz {

	private Map<String, Boolean> ready;
	private Map<String, Integer> scores;
	private Map<String, Integer> combos;
	private Map<String, Boolean> extraLifes;
	private String inviter;
	private boolean accepted;

	public Duel(int len, String userID1, String userID2) {
		super(userID1, len);
		inviter = userID1;
		scores = new HashMap<String, Integer>();
		scores.put(userID1, 0);
		scores.put(userID2, 0);
		combos = new HashMap<String, Integer>();
        combos.put(userID1, 0);
        combos.put(userID2, 0);
		ready = new HashMap<String, Boolean>();
		ready.put(userID1, false);
		ready.put(userID2, false);
        extraLifes = new HashMap<String, Boolean>();
        extraLifes.put(userID1, false);
        extraLifes.put(userID2, false);
	}

	public boolean checkAnswer(String answ, String userID) {
		boolean right = questions.get(quiz[currNumbQuest]).checkAnswer(answ);
		if (right && !ready.get(userID)) {
		    combos.put(userID, combos.get(userID) + 1);
			scores.put(userID, scores.get(userID) + earnedScore * combos.get(userID));
		} else if (!extraLifes.get(userID)) {
		    combos.put(userID, 0);
        }
        extraLifes.put(userID, false);
		ready.put(userID, true);
		nextQuestion();
		if (currNumbQuest >= length && isReady()) {
			end = true;
		}
		return right;
	}

	private void nextQuestion() {
		for (boolean ready: ready.values()) {
			if (!ready) {
				return;
			}
		}
		currNumbQuest += 1;
	}

	public boolean isReady() {
		for (boolean ready: ready.values()) {
			if (!ready) {
				return false;
			}
		}
		return true;
	}

	public boolean isReady(String userID) { return ready.get(userID); }

	public void reset() {
		for (String userID: ready.keySet()) {
			ready.put(userID, false);
		}
	}

	public String getOpponent(String userID1) {
		for (String userID: ready.keySet()) {
			if (!userID.equals(userID1)) {
				return userID;
			}
		}
		return userID1;
	}

    public void activateExtraLife(String userID) { extraLifes.put(userID, true); }

	public void accept() { accepted = true; }

    public boolean isAccepted() { return accepted; }

    public boolean isExtraLifeActive(String userID) { return extraLifes.get(userID); }

    public String getInviter() { return inviter; }

	public int getScore(String userID) { return scores.get(userID); }
}