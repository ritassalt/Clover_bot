package test.java;

import main.Bot;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.*;


public class BotTest {	
	private Bot bot = new Bot();
    
	@Test
	public void testBotDecoder1() throws Exception {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442";
		String decoded = bot.decodeString(unicode);
		assertTrue(decoded.equals("Привет"));
	}
	
	@Test
	public void testBotDecoder2() throws Exception {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442,,";
		String decoded = bot.decodeString(unicode);
		assertTrue(decoded.equals("Привет,,"));
	}
	
	@Test
	public void testGetUpdates() throws Exception {
		String res = bot.getUpdates("");
		assertTrue(!res.equals(""));
		assertNotNull(res);
	}
}
