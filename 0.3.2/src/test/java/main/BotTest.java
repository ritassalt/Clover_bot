package main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BotTest {	
	private Bot bot = new Bot();
	private static Bot spyBot;
	private static String user1 = "371010181:Astrom123";
    private static String user2 = "79741493:ritassalt";

    @BeforeEach
    public  void setup() {
        spyBot = spy(Bot.class);
        doNothing().when(spyBot).sendMessage(isA(String.class), isA(String.class));
        doNothing().when(spyBot).processData(isA(String.class));
        doNothing().when(spyBot).sendNewQuestion(isA(String.class));
    }
    
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
    public void testHelpMessagesBot() {
		spyBot.processMessage("/start", "123");
		verify(spyBot, times(1)).sendMessage("123", spyBot.HELLOMESSAGE);
        spyBot.processMessage("/help", "123");
        verify(spyBot, times(1)).sendMessage("123", spyBot.HELPMESSAGE);
        spyBot.processMessage("/helpshop", "123");
        verify(spyBot, times(1)).sendMessage("123", spyBot.SHOPMESSAGE);
    }

    @Test
    public void testAnswer() {
        spyBot.processMessage("/startquiz", "123");
        spyBot.processMessage("5", "123");
        verify(spyBot, times(1)).startQuiz("123");
        verify(spyBot, times(1)).sendMessage("123", "К сожалению, вы ошиблись");
    }

    @Test
    public void testDuelIsNotPossible() {
        spyBot.processMessage("/startquiz", user2);
        spyBot.processMessage("/duel ritassalt", user1);
        verify(spyBot, times(1)).sendMessage("371010181", "Пользователь в данный момент участвует в викторине");
    }

    @Test
    public void testDuelIsNotPossibleWithSelf() {
        spyBot.processMessage("/duel Astrom123", user1);
        verify(spyBot, times(1)).sendMessage("371010181", "Невозможно вызвать на дуэль самого себя");
    }

    @Test
    public void testDuel() {
	    spyBot.processMessage("/duel ritassalt", user1);
        spyBot.processMessage("/yes", user2);
        spyBot.processMessage("5", user1);
        spyBot.processMessage("5", user2);
        verify(spyBot, times(1)).sendMessage("371010181", "К сожалению, вы ошиблись");
        verify(spyBot, times(1)).sendMessage("79741493", "К сожалению, вы ошиблись");
    }

    @Test
    public void testLife() {
        spyBot.processMessage("/buy extralife", user1);
        spyBot.processMessage("/startquiz", user1);
        spyBot.processMessage("/extralife", user1);
        spyBot.processMessage("/extralife", user1);
        verify(spyBot, times(1)).sendMessage("371010181", "Дополнительная жизнь активирована!");
        verify(spyBot, times(1)).sendMessage("371010181", "Дополнительная жизнь уже активирована");
    }

    @Test
    public void testActivateLifeNotPossibleBeforeQuiz() {
        spyBot.processMessage("/buy extralife", user1);
        spyBot.processMessage("/extralife", user1);
        verify(spyBot, times(1)).sendMessage("371010181", "Нельзя активировать жизнь до начала викторины");
    }

    @Test
    public void testEndDuel() {
        spyBot.processMessage("/duel ritassalt", user1);
        spyBot.processMessage("/yes", user2);
        for (int i = 0; i < 12; i++) {
            spyBot.processMessage("5", user1);
            spyBot.processMessage("5", user2);
        }
        verify(spyBot, times(1)).sendMessage("371010181", "Победила дружба!");
        verify(spyBot, times(1)).sendMessage("79741493", "Победила дружба!");
    }
}