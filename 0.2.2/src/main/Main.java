package main;

import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;


public class Main 
{
	private static String Offset = "";
	
	public static void main(String[] args) {
		Bot bot = new Bot();
		while (true) {
			Map<String, String> messages = processData(bot.getUpdates(Offset));
			for (String key: messages.keySet())	{
				bot.processMessage(messages.get(key), key);
			}
		}
	}
	
	public static Map<String, String> processData(String data) {
		Map<String, String> res = new HashMap<String, String>();
		String[] data_string = data.split("\"update_id\":");
		data_string[0] = "";
		Pattern userNameP = Pattern.compile("username\\\":\\\"(.+?)\\\",\\\"");
		Pattern p = Pattern.compile("(\\d+?),.+\\\"from\\\":\\{\\\"id\\\":(\\d+),.+text\\\":\\\"(.+?)\\\"[,}]");
		for (String s: data_string) {
			Matcher userNameM = userNameP.matcher(s);
			Matcher m = p.matcher(s);
			if (m.find()) {
				if (userNameM.find()) {
				    res.put(m.group(2) + ":" + userNameM.group(1), m.group(3));
				} else {
					res.put(m.group(2), m.group(3));
				}
				Offset = String.valueOf(Integer.parseInt(m.group(1)) + 1);
			}
		}
		return res;
	}
}
