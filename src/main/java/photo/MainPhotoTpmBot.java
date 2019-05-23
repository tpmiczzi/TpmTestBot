package photo;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.logging.BotLogger;

public class MainPhotoTpmBot {
    private static final String LOG_TAG = "START";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new PhotoTpmBot());
        } catch (TelegramApiRequestException e) {
            BotLogger.error(LOG_TAG, "exception", e);
        }

        BotLogger.info(LOG_TAG,"PhotoBot successfully started!");
    }
}
