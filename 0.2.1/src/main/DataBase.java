package main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {
	
	private Map<String, UserDataObject> usersData = new HashMap<String, UserDataObject>();

	public DataBase() {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get("src\\data.txt"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line: lines) {
			String[] words = line.split(":");
			usersData.put(words[0], new UserDataObject(words[0], words[1], Integer.parseInt(words[2])));
		}
	}
	
	public void addNewUser(String[] data) {
		if (data.length == 2) {
		    usersData.put(data[0], new UserDataObject(data[0], data[1], 0));
		    Save(data[0]);
		} else if (data.length == 1) {
		    usersData.put(data[0], new UserDataObject(data[0], null, 0));
		    Save(data[0]);
		}
	}
	
	public boolean contains(String userID) {
		return usersData.containsKey(userID);
	}
	
	public String find(String username) {
		for (UserDataObject dataObj: usersData.values()) {
			if (dataObj.getUsername().equals(username)) {
				return dataObj.getUserID();
			}
		}
		return null;
	}
	
	public void addScores(String userID, int score) {
		usersData.get(userID).addScore(score);
	}
	
	public void Save() {
		try (FileWriter writer = new FileWriter("src\\data.txt", false)) {
			for (UserDataObject line : usersData.values()) {
				writer.write(line.getUserID() + ':' + line.getUsername() + ':' + line.getScores());
				writer.append('\n');
				writer.flush();
			}			
        } catch (IOException ex) {             
            System.out.println(ex.getMessage());
        }			
	}
	public void Save(String userID) {
		try (FileWriter writer = new FileWriter("src\\data.txt", false)) {
			writer.write(userID + ':' + usersData.get(userID).getUsername() + ':' +
					usersData.get(userID).getScores());
			writer.append('\n');
			writer.flush();
        } catch (IOException ex) {             
            System.out.println(ex.getMessage());
        }			
	}
}
