package main;

import java.util.HashMap;

public class UserDataObject {
	
	private String userID;
	private String username;
	private int scores;
	private HashMap<String, Integer> bonuses;
	
	public UserDataObject(String userID, String username, int scores, int lifesCount) {
		this.userID = userID;
		this.username = username;
		this.scores = scores;
		bonuses = new HashMap<String, Integer>();
		bonuses.put("extralife", lifesCount);
	}
	
	public void addScore(int score) { scores += score; }
	
	public String getUsername() { return username; }
	
	public String getUserID() { return userID; }
	
	public int getScores() { return scores;	}

	public void updateBonuses(String name, Integer price) {
		bonuses.put(name, bonuses.get(name) + 1);
		scores -= price;
	}

	public void useBonus(String name) { bonuses.put(name, bonuses.get(name) - 1); }

	public int getBonusCount(String name) { return bonuses.get(name); }
}
