package main;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;


public class Bot {
	
	static final String TOKEN = "https://api.telegram.org/bot643714257:AAEdIQpa1AOF8ZLeGblfbyFtzr06moeGjqo/";
	static final String HELLOMESSAGE = "Привет! Я - бот, который поможет тебе выигрывать в викторине \"Клевер\". Чтобы познакомиться с тем, что я умею, вызови команду /help";
	static final String HELPMESSAGE = "/startquiz - начать викторину с 12 вопросами и 4 вариантами ответа на каждый, за верный ответ начисляются 10 очков \r\n" + 
			"/showscore - показать набранные очки за прошлую или текущую игру\r\n" + 
			"/start - показать приветственное сообщение \r\n" + 
			"/help - показать эту справку";
	private Map<String, Quiz> quizes;
	private DataBase base = new DataBase();
	
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
		quizes.put(userID, new Quiz(12));
		sendNewQuestion(userID);
	}
	
	public void startDuel(String userID1, String userName2) {
		String userID2 = base.find(userName2);
		if (userID2 == null) {
			sendMessage(userID1, "Пользователь не найден");
			return;
		}
		if (isNotActive(userID1) && isNotActive(userID2)) {
		    Duel duel = new Duel(12, userID1, userID2);
		    quizes.put(userID1, duel);
		    quizes.put(userID2, duel);
		    sendNewQuestion(userID1);
		    sendNewQuestion(userID2);
		}
	}
	
	public void sendNewQuestion(String userID) {
			sendMessage(userID, quizes.get(userID).getCurrQuest());
	}
	
	public void processMessage(String message, String userData) {
		processData(userData);
		String userID = userData.split(":")[0];
		String command[] = message.split(" ");
		switch(command[0]) {
			case "/showscore":
				if (quizes.containsKey(userID)) {
					if (quizes.get(userID) instanceof Duel) {
						sendMessage(userID, "Ваши очки: " + Integer.toString(((Duel)quizes.get(userID)).getScore(userID)));
					} else {
						sendMessage(userID, "Ваши очки: " + Integer.toString(quizes.get(userID).getScore()));
					}
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
				if (isNotActive(userID)) {
					sendMessage(userID, "Начнём!");
					startQuiz(userID);
				} else {
					sendMessage(userID, "Викторина уже идет");
				}
				break;
			case "/duel":
				startDuel(userID, command[1]);
				break;
			default:
				if (quizes.containsKey(userID) && !quizes.get(userID).isEnd()) {
					String userAnswer = decodeString(message);
					processAnswer(userAnswer, userID);
					if (quizes.get(userID).isEnd()) {
						base.addScores(userID, quizes.get(userID).getScore());
						if (quizes.get(userID) instanceof Duel) {
							Duel duel = (Duel) quizes.get(userID);
							String opponent = duel.getOpponent(userID);
							base.addScores(opponent, duel.getScore(opponent));
						}
						base.Save();
					}
				} else {
					sendMessage(userID, HELPMESSAGE);
				}
				break;
		}
	}
	
	private boolean isNotActive(String userID) {
		return (quizes.containsKey(userID) && quizes.get(userID).isEnd) || !quizes.containsKey(userID);
	}
	
	private void processData(String userData) {
		String[] data = userData.split(":");
		if (base.contains(data[0])) {
			return;
		} else {
			base.addNewUser(data);
		}
	}
	
	private void processAnswer(String answ, String userID) {
		Quiz quiz = quizes.get(userID);
		String answer = quiz.getAnswer();
		boolean right = false;
		if (quiz instanceof Duel) {
			right = ((Duel) quiz).checkAnswer(answ, userID);
		} else {
			right = quiz.checkAnswer(answ);
		}
		if (right) {
			sendMessage(userID, "Правильно!");
		} else {
			sendMessage(userID, "К сожалению, вы ошиблись\nПравильный ответ: " + answer);
		}
		if (!quiz.isEnd()) {
			if (quiz instanceof Duel && ((Duel)quiz).getIsReady()) {
				String opponentID = ((Duel)quiz).getOpponent(userID);
				sendNewQuestion(userID);
				sendNewQuestion(opponentID);
				((Duel)quiz).reset();
			} else if (quiz instanceof Duel) {
				sendMessage(userID, "Ожидаем ответа противника...");
			} else if (!(quiz instanceof Duel)) {
				sendNewQuestion(userID);
			}
		} else {
			if (quiz instanceof Duel) {
				Duel duel = (Duel)quiz;
				String opponentID = duel.getOpponent(userID);
				int score1 = duel.getScore(userID);
				int score2 = duel.getScore(opponentID);
				if (score1 > score2) {
					sendMessage(userID, "Поздравляем с победой!");
					sendMessage(opponentID, "К сожалению, вы проиграли");
				} else if (score1 < score2) {
					sendMessage(userID, "К сожалению, вы проиграли");
					sendMessage(opponentID, "Поздравляем с победой!");
				} else {
					sendMessage(userID, "Победила дружба!");
					sendMessage(opponentID, "Победила дружба!");
				}
				sendMessage(userID, "Ваши очки: " + Integer.toString(score1) +
						"\nОчки оппонента: " + Integer.toString(score2));
				sendMessage(opponentID, "Ваши очки: " + Integer.toString(score2) +
						"\nОчки оппонента: " + Integer.toString(score1));
			} else {
			    sendMessage(userID, "Викторина окончена.\nНабранные очки: " +
			                Integer.toString(quiz.getScore()));
			}
		}
	}
	
	public String getUpdates(String offset)	{
		Map<String, String> args = new HashMap<String, String>();
		args.put("offset", offset);
		return URLRequest("getUpdates", args);
	}
	
	public String decodeString(String s) {
		if (!s.contains("\\")) {
			return s;
		}
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