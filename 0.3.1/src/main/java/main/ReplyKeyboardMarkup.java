package main;

public class ReplyKeyboardMarkup {

    private  KeyboardButton[][] keyboard;
    private boolean one_time_keyboard;

    public ReplyKeyboardMarkup(String[] answers) {
        one_time_keyboard = true;
        keyboard = new KeyboardButton[4][];
        if (answers.length == 4) {
            for (int i = 0; i < 4; i++) {
                keyboard[i] = new KeyboardButton[1];
                keyboard[i][0] = new KeyboardButton(answers[i]);
            }
        }
    }
}
