package main;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Bot {
	
	static final String TOKEN = "https://api.telegram.org/bot643714257:AAEdIQpa1AOF8ZLeGblfbyFtzr06moeGjqo/";
	static final String HELLOMESSAGE = "Привет! Я - бот, который поможет тебе выигрывать в викторине \"Клевер\". Чтобы познакомиться с тем, что я умею, вызови команду /help";
	static final String HELPMESSAGE = "/startquiz - начать викторину с 12 вопросами и 4 вариантами ответа на каждый, за верный ответ начисляются 10 очков \r\n" + 
			"/showscore - показать набранные очки за прошлую или текущую игру\r\n" + 
			"/start - показать приветственное сообщение \r\n" + 
			"/help - показать эту справку";
	private Map<String, Quiz> quizes;
	
	public Bot() {
		quizes = new HashMap<String, Quiz>();
	}
	
	public void sendMessage(String chatID, String message) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("chat_id", chatID);
		args.put("text", message);
		URLRequest("sendMessage", args);
	}
	
	public void startQuiz(String userID) {
		quizes.put(userID, new Quiz());
		sendNewQuestion(userID);
	}
	
	public void sendNewQuestion(String userID) {
		sendMessage(userID, quizes.get(userID).getCurrQuest());
	}
	
	public void processMessage(String message, String userID) {
		switch(message) {
			case "/showscore":
				if (quizes.containsKey(userID)) {
					sendMessage(userID, "Ваши очки: " + Integer.toString(quizes.get(userID).getScore()));
				} else {
					sendMessage(userID, "Вы еще не участвовали в викторине");
				}
				break;
			case "/start":
				sendMessage(userID, HELLOMESSAGE);
				break;
			case "/help":
				sendMessage(userID, HELPMESSAGE);
				break;
			case "/startquiz":
				sendMessage(userID, "Начнём!");
				startQuiz(userID);
				break;
			default:
				if (quizes.containsKey(userID) && !quizes.get(userID).isEnd()) {
					String userAnswer = decodeString(message);
					String answer = quizes.get(userID).getAnswer();
					if (quizes.get(userID).checkAnswer(userAnswer)) {
						sendMessage(userID, "Правильно!");
					} else {
						sendMessage(userID, "К сожалению, вы ошиблись\nПравильный ответ: " + answer);
					}
					if (!quizes.get(userID).isEnd()) {
						sendNewQuestion(userID);
					} else {
						sendMessage(userID, "Викторина окончена.\nНабранные очки: " +
						            Integer.toString(quizes.get(userID).getScore()));
					}
				} else {
					sendMessage(userID, HELPMESSAGE);
				}
				break;
		}
	}
	
	public String getUpdates(String offset)	{
		Map<String, String> args = new HashMap<String, String>();
		args.put("offset", offset);
		return URLRequest("getUpdates", args);
	}
	
	public String decodeString(String s) {
		StringBuilder res = new StringBuilder();
		s = s.replace("\\", "");
		String[] arr = s.split("u");
		String symbs = "";
		for(int i = 1; i < arr.length; i++)	{
			if(arr[i].length() >= 5) {
				symbs = arr[i].substring(4, arr[i].length());
				arr[i] = arr[i].substring(0, 4);
			} else {
				symbs = "";
			}
			int hexVal = Integer.parseInt(arr[i], 16);
			res.append((char)hexVal);
			if (symbs != "") {
				res.append(symbs);
			}
		}
		return res.toString();
	}
	
	public String URLRequest(String methodName, Map<String, String> args) {
		StringBuilder s_url = new StringBuilder();
		s_url.append(TOKEN);
		s_url.append(methodName);
		try {
			if (args != null) {
				s_url.append("?");
				for (String key: args.keySet())	{
					s_url.append(key);
					s_url.append("=" + URLEncoder.encode(args.get(key), "UTF-8") + "&");
				}
				s_url.deleteCharAt(s_url.length() - 1);
			}
			URL url = new URL(s_url.toString());
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
