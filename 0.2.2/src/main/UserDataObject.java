package main;

public class UserDataObject {
	
	private String userID;
	private String username;
	private int scores;
	
	public UserDataObject(String userID, String username, int scores) {
		this.userID = userID;
		this.username = username;
		this.scores = scores;
	}
	
	public void addScore(int score) {
		scores += score;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public int getScores() {
		return scores;
	}
}
