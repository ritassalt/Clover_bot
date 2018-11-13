package main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class BotTest {	
	private Bot bot = new Bot();
    
	@Test
	public void testBotDecoder1() {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442";
		String decoded = bot.decodeString(unicode);
		assertEquals("Привет", decoded);
	}
	
	@Test
	public void testBotDecoder2() {
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442,,";
		String decoded = bot.decodeString(unicode);
		assertEquals("Привет,,", decoded);
	}
	
	@Test
	public void testGetUpdates() {
		String res = bot.getUpdates("");
		assertTrue(!res.equals(""));
		assertNotNull(res);
	}

	@Test
    public void testBot() {
        Bot bot = mock(Bot.class);
    }
}