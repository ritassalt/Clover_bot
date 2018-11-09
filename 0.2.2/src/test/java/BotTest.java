package test.java;

import main.Bot;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;


public class BotTest {	
	private Bot bot = new Bot();
    
	@Test
	public void testBotDecoder1() throws Exception {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442";
		String decoded = bot.decodeString(unicode);
		assertEquals(decoded, "Привет");
	}
	
	@Test
	public void testBotDecoder2() throws Exception {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442,,";
		String decoded = bot.decodeString(unicode);
        assertEquals(decoded, "Привет,,");
	}
	
	@Test
	public void testGetUpdates() throws Exception {
		String res = bot.getUpdates("");
		assertTrue(!res.equals(""));
		assertNotNull(res);
	}
}