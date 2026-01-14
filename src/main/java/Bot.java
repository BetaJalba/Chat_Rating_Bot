import okhttp3.*;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final BotFunctions botFunctions = new BotFunctions();

    public Bot(String botToken) throws SQLException {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    // Main method
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            long chatId = update.getMessage().getChatId();

            SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();
            builder.text(analyzeText(msg));
            builder.chatId(chatId);
            sendResponse(builder.build());
        }
    }

    private String analyzeText(Message msg) {
        if (!msg.hasText())
            return "";

        if (msg.getText().startsWith("/")) {
            switch (msg.getText()) {
                case "/leaderboard" -> {
                    return botFunctions.displayLeaderboard(msg);
                }
                case "/score" -> {
                    return botFunctions.displayUserScore(msg);
                }
                case "/chat_messages" -> {
                    return botFunctions.displayMessageCountChat(msg);
                }
                case "/all_user_messages" -> {
                    return botFunctions.displayTotalMessagesUser(msg);
                }
                case "/mean_user_behavior" -> {
                    return botFunctions.displayMeanBehaviorScoreUser(msg);
                }
                case "/mean_global_behavior" -> {
                    return botFunctions.displayMeanBehaviorScoreAllChats();
                }
                case "/help" -> {
                    return botFunctions.displayHelp();
                }
                default -> {
                    return "Sorry, I don't seem to recognize this command";
                }
            }
        }

        return botFunctions.standardProcedure(msg);
    }

    private void sendResponse(SendMessage response) {
        if (response.getText().isEmpty())
            return;

        try {
            telegramClient.execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
