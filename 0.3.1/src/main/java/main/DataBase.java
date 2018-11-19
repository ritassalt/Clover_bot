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
	private String path;

	public DataBase(String basePath) {
		path = basePath;
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get("src\\" + path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String line: lines) {
				if (!line.equals("")) {
					String[] words = line.split(":");
					usersData.put(words[0], new UserDataObject(words[0], words[1], Integer.parseInt(words[2])));
			}
		}
	}
	
	public void addNewUser(String[] data) {
		if (data.length == 2) {
		    usersData.put(data[0], new UserDataObject(data[0], data[1], 0));
		    Save(data[0]);
		} else if (data.length == 1) {
		    usersData.put(data[0], new UserDataObject(data[0], "", 0));
		    Save(data[0]);
		}
	}
	
	public boolean contains(String userID) { return usersData.containsKey(userID); }
	
	public String find(String username) {
		for (UserDataObject dataObj: usersData.values()) {
			if (dataObj.getUsername().equals(username)) {
				return dataObj.getUserID();
			}
		}
		return null;
	}

	public UserDataObject getUserData(String userID) { return usersData.get(userID); }

	public String getUsername(String userID) { return usersData.get(userID).getUsername(); }
	
	public void addScores(String userID, int score) { usersData.get(userID).addScore(score); }
	
	public void Save() {
		try (FileWriter writer = new FileWriter("src\\" + path, false)) {
			for (UserDataObject line : usersData.values()) {
				writer.write(line.getUserID() + ':' + line.getUsername() + ':' + 
						Integer.toString(line.getScores()) + '\n');
			}			
        } catch (IOException ex) {             
            System.out.println(ex.getMessage());
        }			
	}
	public void Save(String userID) {
		try (FileWriter writer = new FileWriter("src\\" + path, true)) {
			writer.write(userID + ":" + usersData.get(userID).getUsername() + ":" +
					Integer.toString(usersData.get(userID).getScores()) + "\n");
        } catch (IOException ex) {             
            System.out.println(ex.getMessage());
        }			
	}
	
	public int getScore(String userID) { return usersData.get(userID).getScores(); }
}
