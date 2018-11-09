import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.junit.jupiter.api.Test;


public class BotTest
{	
	private Bot bot = null;
	@Before
    public void init() { bot = new Bot(); }
    @After
    public void tearDown() { bot = null; }
    
	@Test
	public void TestBotDecoder1() throws Exception 
	{
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442";
		String decoded = bot.DecodeString(unicode);
		assertTrue(decoded.equals("Привет"));
	}
	
	@Test
	public void TestBotDecoder2() throws Exception 
	{
		String unicode = "\\u041f\\u0440\\u0438\\u0432\\u0435\\u0442,,";
		String decoded = bot.DecodeString(unicode);
		assertTrue(decoded.equals("Привет,,"));
	}
	
	@Test
	public void TestGetUpdates() throws Exception
	{
		String res = bot.GetUpdates("");
		assertTrue(!res.equals(""));
		assertNotNull(res);
	}
}
