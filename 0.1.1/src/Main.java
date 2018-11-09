import java.util.regex.*;
import java.util.Map;
import java.util.HashMap;


public class Main 
{
	private static String Offset = "";
	
	public static void main(String[] args)
	{
		Bot bot = new Bot();
		while (true)
		{
			Map<String, String> messages = ProcessData(bot.GetUpdates(Offset));
			for (String key: messages.keySet())
			{
				bot.ProcessMessage(messages.get(key), key);
			}
		}
	}
	
	public static Map<String, String> ProcessData(String data)
	{
		Map<String, String> res = new HashMap<String, String>();
		String[] data_string = data.split("\"update_id\":");
		data_string[0] = "";
		Pattern p = Pattern.compile("(\\d+?),.+\\\"from\\\":\\{\\\"id\\\":(\\d+),.+text\\\":\\\"(.+?)\\\"[,}]");
		for (String s: data_string)
		{
			Matcher m = p.matcher(s);
			if (m.find())
			{
				res.put(m.group(2), m.group(3));
				Offset = String.valueOf(Integer.parseInt(m.group(1)) + 1);
			}
		}
		return res;
	}
}
