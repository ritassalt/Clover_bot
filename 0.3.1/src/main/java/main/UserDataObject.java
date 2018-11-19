package main;

import java.util.HashMap;

public class UserDataObject {
	
	private String userID;
	private String username;
	private int scores;
	private HashMap<String, Integer> bonuses;
	
	public UserDataObject(String userID, String username, int scores) {
		this.userID = userID;
		this.username = username;
		this.scores = scores;
	}
	
	public void addScore(int score) { scores += score; }
	
	public String getUsername() { return username; }
	
	public String getUserID() { return userID; }
	
	public int getScores() { return scores;	}

	public void updateBonuses(String name, Integer value, Integer price) {
		bonuses.put(name, value);
		scores -= price;
	}

	public int getBonusCount(String name) { return bonuses.get(name); }
}