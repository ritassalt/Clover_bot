package main;

public class ReplyKeyboardMarkup {

    private  KeyboardButton[][] keyboard;

    public ReplyKeyboardMarkup() {
        keyboard = new KeyboardButton[3][];
        for (int i = 0; i < 2; i++) {
            keyboard[i] = new KeyboardButton[2];
        }
            keyboard[0][0] = new KeyboardButton("1");
            keyboard[0][1] = new KeyboardButton("2");
            keyboard[1][0] = new KeyboardButton("3");
            keyboard[1][1] = new KeyboardButton("4");
            keyboard[2] = new KeyboardButton[1];
            keyboard[2][0] = new KeyboardButton("/extralife");
    }
}
