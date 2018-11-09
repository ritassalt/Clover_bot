package test.java;

import main.Bot;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;


public class BotTest {	
	private Bot bot = new Bot();
    
	@Test
	public void TestBotDecoder1() throws Exception {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442";
		String decoded = bot.decodeString(unicode);
		assertTrue(decoded.equals("Привет"));
	}
	
	@Test
	public void TestBotDecoder2() throws Exception {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442,,";
		String decoded = bot.decodeString(unicode);
		assertTrue(decoded.equals("Привет,,"));
	}
	
	@Test
	public void TestGetUpdates() throws Exception {
		String res = bot.getUpdates("");
		assertTrue(!res.equals(""));
		assertNotNull(res);
	}
}
