package main;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import com.google.gson.Gson;


public class Bot {

	private static Shop shop = new Shop();
	private static String token;
	public static final String HELLOMESSAGE = "Привет! Я - бот, который поможет тебе выигрывать в викторине \"Клевер\". Чтобы познакомиться с тем, что я умею, вызови команду /help";
	public static final String HELPMESSAGE = "/startquiz - начать викторину с 12 вопросами и 4 вариантами ответа на каждый, за верный ответ начисляются 10 очков, умножаемые на комбо\r\n" +
			"/showscore - показать очки и кол-во дополнительных жизней\r\n" +
			"/start - показать приветственное сообщение\r\n" +
			"/help - показать эту справку\r\n" +
			"/duel username - начать дуэль с пользователем @username\r\n" +
            "/buy item - купить определенный бонус за имеющиеся очки (/helpshop - справка по магазину)";
    public static final String SHOPMESSAGE = "Для покупки бонуса введите команду /buy и название бонуса.\r\n" +
            "В наличии:\r\n" +
            "extralife (200 очков) - возможность один раз за игру сохранить уровень комбо-очков при неверном ответе";
    private Map<String, Quiz> quizes;
    private static String keyboard;
	private DataBase base = new DataBase("D:\\botdata\\data.txt");

	public Bot() {
	    quizes = new HashMap<String, Quiz>();
	    token = getToken();
	    loadQuizes();
        Gson gson = new Gson();
	    keyboard = gson.toJson(new ReplyKeyboardMarkup(), ReplyKeyboardMarkup.class);
	}

	public void sendMessage(String chatID, String message) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("chat_id", chatID);
		args.put("text", message);
		if (isNotActive(chatID)){
		    args.put("reply_markup", "{\"remove_keyboard\": true}");
        }
		URLRequest("sendMessage", args);
	}

	public void startQuiz(String userID) {
		quizes.put(userID, new Quiz(userID, 12));
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
		args.put("reply_markup", keyboard);
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
				sendMessage(userID, "Ваши жизни: " + base.getUserData(userID).getBonusCount("extralife"));
				break;
			case "/start":
				sendMessage(userID, HELLOMESSAGE);
				break;
			case "/help":
				sendMessage(userID, HELPMESSAGE);
				break;
			case "/helpshop":
                sendMessage(userID, SHOPMESSAGE);
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
            case "/extralife":
                activateExtraLife(userID);
                break;
			case "/duel":
			    if (command.length > 1) {
                    inviteOnDuel(userID, command[1]);
                } else {
                    sendMessage(userID, "Необходимо указать username оппонента!");
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
		return !quizes.containsKey(userID) || quizes.get(userID).end;
	}

	public void processData(String userData) {
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
            sendMessage(userID, "Указывайте название товара в команде (/helpshop для справки)");
            return;
        }
        sendMessage(userID, shop.buy(base.getUserData(userID), item));
    }

    private void activateExtraLife(String userID) {
	    if (base.getUserData(userID).getBonusCount("extralife") == 0) {
	        sendMessage(userID, "На вашем счету нет дополнительных жизней!");
	        return;
        }
        if (isNotActive(userID)) {
        	sendMessage(userID, "Нельзя активировать жизнь до начала викторины");
        	return;
		}
        Quiz quiz = quizes.get(userID);
	    if (quiz instanceof Duel && !((Duel)quiz).isExtraLifeActive(userID) || !quiz.isExtraLifeActive()) {
	        if (quiz instanceof Duel) {
                if (((Duel)quiz).isAccepted()) {
                    ((Duel) quiz).activateExtraLife(userID);
                    base.getUserData(userID).useBonus("extralife");
                    sendMessage(userID, "Дополнительная жизнь активирована!");
                } else {
                    sendMessage(userID, "Нельзя активировать жизнь до начала дуэли");
                }
            } else {
                quiz.activateExtraLife();
                base.getUserData(userID).useBonus("extralife");
                sendMessage(userID, "Дополнительная жизнь активирована!");
            }
        } else {
	        sendMessage(userID, "Дополнительная жизнь уже активирована");
        }
    }

    public void saveQuizes() {
	    File save = new File("D:\\botdata\\save.txt");
	    Gson gson = new Gson();
	    try {
            save.createNewFile();
            FileWriter writer = new FileWriter(save);
            for (Quiz quiz: quizes.values()) {
                if (quiz != null) {
                    if (quiz instanceof Duel) {
                        writer.write("Duel@" + gson.toJson(quiz) + "#");
                    } else {
                        writer.write("Quiz@" + gson.toJson(quiz) + "#");
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
	        e.printStackTrace();
        }
    }

    private void loadQuizes() {
	    File save = new File("D:\\botdata\\save.txt");
	    Gson gson = new Gson();
	    if (save.exists()) {
	        try {
                String data = new String(Files.readAllBytes(Paths.get("D:\\botdata\\save.txt")), StandardCharsets.UTF_8);
                String[] json_quizes = data.split("#");
                for (String json_quiz: json_quizes) {
                    if (!json_quiz.equals("")) {
                        String[] quiz_data = json_quiz.split("@");
                        if (quiz_data[0].equals("Quiz")) {
                            Quiz quiz = gson.fromJson(quiz_data[1], Quiz.class);
                            quizes.put(quiz.getUserID(), quiz);
                        } else {
                            Duel duel = gson.fromJson(quiz_data[1], Duel.class);
                            quizes.put(duel.getInviter(), (Quiz) duel);
                            quizes.put(duel.getOpponent(duel.getInviter()), (Quiz) duel);
                        }
                    }
                }
                save.delete();
            } catch (IOException e) {
	            e.printStackTrace();
            }
        }
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

	private static String getToken() {
        try {
            InputStream iStream = Bot.class.getClassLoader().getResourceAsStream("token.txt");
            byte[] data = new byte[iStream.available()];
            iStream.read(data);
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
}