package echo;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class MainEchoTpmBot {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new EchoTpmBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        System.out.println("PhotoBot successfully started!");
    }
}
