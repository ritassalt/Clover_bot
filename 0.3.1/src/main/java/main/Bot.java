package main;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Bot {

	static Shop shop = new Shop();
	static String token;
	static final String HELLOMESSAGE = "Привет! Я - бот, который поможет тебе выигрывать в викторине \"Клевер\". Чтобы познакомиться с тем, что я умею, вызови команду /help";
	static final String HELPMESSAGE = "/startquiz - начать викторину с 12 вопросами и 4 вариантами ответа на каждый, за верный ответ начисляются 10 очков \r\n" +
			"/showscore - показать набранные очки за прошлую или текущую игру\r\n" +
			"/start - показать приветственное сообщение\r\n" +
			"/help - показать эту справку\r\n" +
			"/duel username - начать дуэль с пользователем @username";
	private Map<String, Quiz> quizes;
	private DataBase base = new DataBase("data.txt");

	public Bot() {
	    try {
            File file = new File("src\\token.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            token = new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
	        e.printStackTrace();
        }
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

	public void inviteOnDuel(String userID1, String userName2) {
		String userID2 = base.find(userName2);
		if (userID2 == null) {
			sendMessage(userID1, "Пользователь не найден");
			return;
		}
		if (userID1.equals(userID2)) {
			sendMessage(userID1, "Невозможно вызвать на дуэль самого себя");
			return;
		}
		if (isNotActive(userID1) && isNotActive(userID2)) {
			Duel duel = new Duel(12, userID1, userID2);
			quizes.put(userID1, duel);
			quizes.put(userID2, duel);
			String userName1;
			if (base.getUsername(userID1).equals("")) {
                userName1 = "пользователь, у которого нет username";
            } else{
                userName1 = "@" + base.getUsername(userID1);
            }
			sendMessage(userID2, "Вас вызвал на дуэль " + userName1 + ". Принять вызов?");
		} else {
			if (!isNotActive(userID1)) {
				sendMessage(userID1, "Викторина уже идёт");
			} else if (!isNotActive(userID2)) {
				sendMessage(userID1, "Пользователь в данный момент участвует в викторине");
			}
		}
	}

	private void acceptInviteOnDuel(String userID) {
        if (checkDuelIsPossible(userID)) {
            Duel duel = (Duel)quizes.get(userID);
            if (!(userID.equals(duel.getInviter()))) {
                duel.accept();
                sendMessage(duel.getOpponent(userID), "Ваша дуэль была принята");
                sendNewQuestion(userID);
                sendNewQuestion(duel.getOpponent(userID));
            }
        }
    }

    private void declineInviteOnDuel(String userID) {
        if (checkDuelIsPossible(userID)) {
            Duel duel = (Duel)quizes.get(userID);
            String opponentID = duel.getOpponent(userID);
            quizes.remove(userID);
            quizes.remove(opponentID);
            sendMessage(userID, "Вызов отменен");
            sendMessage(opponentID, "Вызов отменен");
        }
    }

    private boolean checkDuelIsPossible(String userID) {
        if (!(quizes.get(userID) instanceof Duel)) {
            sendMessage(userID, "Вас не вызывали на дуэль");
            return false;
        }
        Duel duel = (Duel)quizes.get(userID);
        if (duel.isAccepted()) {
            sendMessage(userID, "Дуэль уже идёт");
            return false;
        }
        return true;
    }

	public void sendNewQuestion(String userID) {
		Map<String, String> args = new HashMap<String, String>();
        Quiz quiz = quizes.get(userID);
		args.put("chat_id", userID);
		args.put("text", quiz.getCurrQuest());
		args.put("reply_markup", quiz.getKeyboard());
		URLRequest("sendMessage", args);
	}

	public void processMessage(String message, String userData) {
		processData(userData);
		String userID = userData.split(":")[0];
		String command[] = message.split(" ");
		switch(command[0]) {
			case "/showscore":
				if (quizes.containsKey(userID) && !quizes.get(userID).isEnd()) {
					if (quizes.get(userID) instanceof Duel) {
						sendMessage(userID, "Ваши очки: " + Integer.toString(((Duel)quizes.get(userID)).getScore(userID) + base.getScore(userID)));
					} else {
						sendMessage(userID, "Ваши очки: " + Integer.toString(quizes.get(userID).getScore() + base.getScore(userID)));
					}
				} else {
					sendMessage(userID, "Ваши очки: " + Integer.toString(base.getScore(userID)));
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
            case "/yes":
                acceptInviteOnDuel(userID);
                break;
            case "/no":
                declineInviteOnDuel(userID);
                break;
            case "/buy":
                if (command.length > 1) {
                    buy(userID, command[1]);
                } else {
                    buy(userID, "");
                }
                break;
			case "/duel":
			    if (command.length > 1) {
                    inviteOnDuel(userID, command[1]);
                } else {
                    sendMessage(userID, "Invalid command");
                }
				break;
			default:
				if (quizes.containsKey(userID) && !quizes.get(userID).isEnd()) {
					String userAnswer = decodeString(message);
					processAnswer(userAnswer, userID);
					if (quizes.get(userID).isEnd()) {
						if (quizes.get(userID) instanceof Duel) {
							Duel duel = (Duel) quizes.get(userID);
							String opponent = duel.getOpponent(userID);
							base.addScores(opponent, duel.getScore(opponent));
							base.addScores(userID, duel.getScore(userID));
						} else {
							base.addScores(userID, quizes.get(userID).getScore());
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
		return !quizes.containsKey(userID) || quizes.get(userID).isEnd;
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
			if (!((Duel)quiz).isReady(userID) && ((Duel)quiz).isAccepted()) {
                right = ((Duel) quiz).checkAnswer(answ, userID);
                sendResult(right, userID, answer);
            } else if (!((Duel)quiz).isAccepted()) {
			    if (userID.equals(((Duel)quiz).getInviter())) {
                    sendMessage(userID, "Ждём решения противника...");
                } else {
                    sendMessage(userID, "Примите или отклоните вызов на дуэль командой /yes или /no");
                }
            }
        } else {
			right = quiz.checkAnswer(answ);
            sendResult(right, userID, answer);
		}
		if (!quiz.isEnd()) {
			if (quiz instanceof Duel && ((Duel)quiz).isReady()) {
				String opponentID = ((Duel)quiz).getOpponent(userID);
				sendNewQuestion(userID);
				sendNewQuestion(opponentID);
				((Duel)quiz).reset();
			} else if (quiz instanceof Duel && ((Duel)quiz).isAccepted()) {
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

	public String getUpdates(String offset) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("offset", offset);
		return URLRequest("getUpdates", args);
	}

	public void sendResult(boolean right, String userID, String answer) {
        if (right) {
            sendMessage(userID, "Правильно!");
        } else {
            sendMessage(userID, "К сожалению, вы ошиблись");
            sendMessage(userID, "Правильный ответ: " + answer);
        }
    }

    public void buy(String userID, String item) {
        if (item.equals("")) {
            sendMessage(userID, "Магвзин");
            return;
        }
        sendMessage(userID, shop.buy(base.getUserData(userID), item));
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
			if (!symbs.equals("")) {
				res.append(symbs);
			}
		}
		return res.toString();
	}

	public String URLRequest(String methodName, Map<String, String> args) {
		StringBuilder s_url = new StringBuilder();
		s_url.append(token);
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