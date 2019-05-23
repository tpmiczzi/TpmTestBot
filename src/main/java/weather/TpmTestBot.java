package weather;

import java.io.IOException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

public class TpmTestBot extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(TpmTestBot.class.getName());
    private static final String BOT_TOKEN = "773040857:AAFLbYzDgmzvgbzIyAdpgTNLgmGuB8jSxiM";
    private static final String BOT_NAME = "TpmTestBot";
    private static final String DATE_PATTERN = "dd-MM-YYYY HH:mm:ss";
    private static final String COMMAND_MY_NAME = "/myname";
    private static final String COMMAND_WEATHER = "/weather";
    private static final String COMMAND_HELP = "/help";
    private static final String COMMAND_SETTING = "/setting";

    public void onUpdateReceived(Update update) {
        ModelAnswer modelAnswer = new ModelAnswer();

        Message inputMessage = update.getMessage();
        String timeStamp = new SimpleDateFormat(DATE_PATTERN).format(Calendar.getInstance().getTime());
        LOG.info("TimeStamp - " + timeStamp + " user = " + inputMessage.getFrom().getUserName());

        if (inputMessage.hasText()) {
            switch (inputMessage.getText()) {
                case COMMAND_HELP:
                    sendMsg(inputMessage, "Can i help you?");
                    break;
                case COMMAND_SETTING:
                    sendMsg(inputMessage, "What will be changing?");
                    break;
                case COMMAND_MY_NAME:
                    sendMsg(inputMessage, "Hello " + inputMessage.getFrom().getFirstName());
                    break;
                case COMMAND_WEATHER:
                    sendMsg(inputMessage, "Please, input city");
//                    try {
//                        sendMsg(inputMessage, weather.Weather.getWeather(inputMessage.getText(), modelAnswer));
//                    } catch (IOException e) {
//                        LOG.warning(e.getMessage());
//                        e.printStackTrace();
//                        sendMsg(inputMessage, "Error. City not found!");
//                    }
                    break;
                default:
                    try {
                        sendMsg(inputMessage, Weather.getWeather(inputMessage.getText(), modelAnswer));
                    } catch (IOException e) {
                        LOG.warning(e.getMessage());
                        e.printStackTrace();
                        sendMsg(inputMessage, "Error. City not found!");
                    }
                    break;
            }
        }
    }

    private void sendMsg(Message inputMessage, String text) {
        SendMessage sendMessage = new SendMessage();
//        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(inputMessage.getChatId());
//        sendMessage.setReplyToMessageId(inputMessage.getMessageId());
        sendMessage.setText(text);

        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        KeyboardRow keyboardRowFirst = new KeyboardRow();
        keyboardRowFirst.add(new KeyboardButton(COMMAND_WEATHER));
        keyboardRowFirst.add(new KeyboardButton(COMMAND_MY_NAME));

        KeyboardRow keyboardRowSecond = new KeyboardRow();
        keyboardRowSecond.add(new KeyboardButton(COMMAND_HELP));
        keyboardRowSecond.add(new KeyboardButton(COMMAND_SETTING));

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(keyboardRowFirst);
        keyboardRowList.add(keyboardRowSecond);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public String getBotUsername() {
        return BOT_NAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }
}
