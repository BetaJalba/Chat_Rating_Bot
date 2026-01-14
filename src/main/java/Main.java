import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ConfigurationService configurationService = ConfigurationService.getInstance();

        String botToken = configurationService.getProperty("BOT_TOKEN");
        assert (botToken != null);

        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new Bot(botToken));
            String botName = configurationService.getProperty("BOT_NAME") != null ? configurationService.getProperty("BOT_NAME") : "Bot";
            System.out.println( configurationService.getProperty("BOT_NAME") + " successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
