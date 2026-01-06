import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ConfigurationService configurationService = ConfigurationService.getInstance();
        DataService dataService;

        try {
            dataService = DataService.getInstance();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        String botToken = configurationService.getProperty("BOT_TOKEN");

        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new Bot(botToken));
            System.out.println("MyAmazingBot successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
