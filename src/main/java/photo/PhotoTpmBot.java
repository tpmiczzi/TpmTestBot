package photo;

import static java.lang.Math.toIntExact;

import com.vdurmont.emoji.EmojiParser;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

public class PhotoTpmBot extends TelegramLongPollingBot {

    private static final String LOG_TAG = "class PhotoTpmBot";
    private static final String BOT_TOKEN = "798980284:AAEQFK7FchjWD6o6mFZpe6BKWtL7GAE06zw";
    private static final String BOT_NAME = "PhotoTpmBot";
    private static final String UPDATE_MSG_TEXT = "update_msg_text";

    private List<PhotoSize> listPhotos = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        BotLogger.info(LOG_TAG, "get call");

        if (update.hasMessage() && update.getMessage().hasText()) {
            workWithMessage(update);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            workWithPhoto(update);
        } else if (update.hasCallbackQuery()) {
            workWithCallbackMsg(update);
        }
    }

    private void workWithCallbackMsg(Update update) {
        String callData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callData.equals(UPDATE_MSG_TEXT)) {
            String botAnswer = "cool";
            sendMsgEdit(messageId, chatId, botAnswer);

            BotLogger.info(LOG_TAG, "update message");
        }
    }

    private void sendMsgEdit(long messageId, long chatId, String answer) {
        EditMessageText newMessage = new EditMessageText()
                .setChatId(chatId)
                .setMessageId(toIntExact(messageId))
                .setText(answer);

        try {
            execute(newMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOG_TAG, "exception", e);
        }
    }

    private void workWithPhoto(Update update) {
        long chatId = update.getMessage().getChatId();
        List<PhotoSize> photos = update.getMessage().getPhoto();

        PhotoSize photoBigSize = photos.stream()
                                       .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                                       .findFirst()
                                       .get();

        listPhotos.add(photoBigSize);
        sendMsg("Photo saved.", chatId, null);
    }

    private void workWithMessage(Update update) {
        String botAnswer = EmojiParser.parseToUnicode("Hello :smile: my dear friend :alien:");
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        String userName = update.getMessage().getFrom().getUserName();

        if (messageText.equals("/start")) {
            sendMsg(botAnswer, chatId, getInlineKeyboardMarkup());

            DatabaseService databaseService = new DatabaseService();

            try {
                databaseService.insertWithStatement(update.getMessage().getFrom().getId() ,userName);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            BotLogger.info(LOG_TAG, createLogMessage(messageText, botAnswer, userName));
        } else if (messageText.equals("/pic")) {
            if (!listPhotos.isEmpty()) {
                String photoId = getRandomPhoto();
                String photoCaption = "hello :)";

                sendPhoto(chatId, photoId, photoCaption);

                BotLogger.info(LOG_TAG, createLogMessage(messageText, "sent photo id - " + photoId, userName));
            } else {
                botAnswer = "Photos haven't downloaded yet. Please, first upload.";
                sendMsg(botAnswer, chatId, null);

                BotLogger.info(LOG_TAG, createLogMessage(messageText, botAnswer, userName));
            }
        } else if (messageText.equals("/markup")) {
            botAnswer = "here keyboard";
            sendMsg(botAnswer, chatId, getReplyKeyboardMarkup());

            BotLogger.info(LOG_TAG, createLogMessage(messageText, botAnswer, userName));
        } else if (messageText.equals("/hide")) {
            botAnswer = "keyboard is hide";

            sendMsg(botAnswer, chatId, new ReplyKeyboardRemove());

            BotLogger.info(LOG_TAG, createLogMessage(messageText, botAnswer, userName));
        } else {
            botAnswer = "Unknown command";

            sendMsg(botAnswer, chatId, getReplyKeyboardMarkup());

            BotLogger.info(LOG_TAG, createLogMessage(messageText, botAnswer, userName));
        }
    }

    private ReplyKeyboard getInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton().setText("Update message text").setCallbackData(UPDATE_MSG_TEXT));
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private String createLogMessage(String messageText, String botAnswer, String userName) {
        return String.format("request message \"%s\"; response message \"%s\"; user \"%s\"",
                             messageText,
                             botAnswer,
                             userName);
    }

    private String getRandomPhoto() {
        PhotoSize photoSize = listPhotos.get(new Random().nextInt(listPhotos.size()));

        return photoSize.getFileId();
    }

    private ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button, you can also use KeyboardButton objects if you need something else than text
        row.add("/pic");
        row.add("/hide");
        row.add("/markup");
        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        // Set each button for the second line
        row.add("is empty");
        row.add("is empty");
        row.add("is empty");
        // Add the second row to the keyboard
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    private void sendPhoto(long chatId, String photoId, String photoCaption) {
        SendPhoto msg = new SendPhoto()
                .setChatId(chatId)
                .setPhoto(photoId)
                .setCaption(photoCaption);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            BotLogger.error(LOG_TAG, "exception", e);
        }
    }

    private void sendMsg(String messageText, long chatId, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(messageText);

        if (Objects.nonNull(replyKeyboard)) {
            message.setReplyMarkup(replyKeyboard);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            BotLogger.error(LOG_TAG, "exception", e);
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
