import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.sql.SQLException;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final DataService dataService = DataService.getInstance();
    private final ConfigurationService configurationService = ConfigurationService.getInstance();

    public Bot(String botToken) throws SQLException {
        telegramClient = new OkHttpTelegramClient(botToken);
    }


    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            User user = update.getMessage().getFrom();

            SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();
            builder.text("");

            if (!user.getIsBot()){
                long userId = user.getId();

                // If it doesn't recogize the user add it to the database
                // Else increment message count
                if (!dataService.checkUser(userId)){
                    builder.text(configurationService.getProperty("WELCOME_MESSAGE"));
                    dataService.insertUser(userId);
                    System.out.println(dataService.getUsers());
                }
                else{
                    dataService.incrementUserMessages(userId);
                }
            }
            long chatId = update.getMessage().getChatId();
            builder.chatId(chatId);

            SendMessage reply = builder.build();
            if (!reply.getText().isBlank())
                sendResponse(reply);
        }
    }

    private void sendResponse(SendMessage response) {
        try {
            telegramClient.execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
