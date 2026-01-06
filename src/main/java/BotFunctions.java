import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.sql.SQLException;
import java.util.ArrayList;

public class BotFunctions {
    private final DataService dataService = DataService.getInstance();
    private final ConfigurationService configurationService = ConfigurationService.getInstance();
    private final BehaviorRater behaviorRater = new BehaviorRater();

    public BotFunctions() throws SQLException {
    }

    public String standardProcedure(Message message) {
        User user = message.getFrom();
        String resp = "";

        if (user.getIsBot())
            return resp;

        long userId = user.getId();
        long chatId = message.getChatId();

        if (!dataService.checkChat(chatId)){
            dataService.insertChat(chatId);
        }
        if (!dataService.checkUser(userId)){
            dataService.insertUser(userId, user.getUserName() != null ? user.getUserName() : null);
            resp = configurationService.getProperty("WELCOME_MESSAGE");
        }
        if (!dataService.checkUserInChat(userId, chatId))
            dataService.addParticipation(userId, chatId);
        dataService.incrementUserMessages(userId, chatId);
        behaviorRater.rateMessage(message.getText(), userId, chatId);

        return resp;
    }

    public String displayLeaderboard(Message message) {
        long chatId = message.getChatId();
        return dataService.getChatLeaderboard(chatId);
    }

    public String displayUserScore(Message message) {
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();
        return String.valueOf(dataService.getUserScore(userId, chatId));
    }

    public String displayHelp(){
        return """
                Commands:
                    /leaderboard    - chat leaderboard
                    /score  - user score in chat
                    /help   - help
                    /chat_messages  - number of messages sent in a chat
                    /all_user_messages  - number of messages sent by a user globally
                    /mean_user_behavior - mean score of user globally
                    /mean_global_behavior   - mean score of chats globally
                """;
    }

    public String displayMessageCountChat(Message message) {
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();
        return String.valueOf(dataService.getMessageCountUserInChat(userId, chatId));
    }

    public String diaplayMessageCountUserInChat(){
        return "";
    }

    public String displayTotalMessagesUser(Message message) {
        long userId = message.getFrom().getId();
        return String.valueOf(dataService.getTotalMessagesUser(userId));
    }

    public String displayMeanBehaviorScoreUser(Message message) {
        long userId = message.getFrom().getId();
        return String.format("%.2f", dataService.getMeanBehaviorScoreUser(userId));
    }

    public String displayMeanBehaviorScoreAllChats() {
        return String.format("%.2f", dataService.getMeanBehaviorScoreAllChats());
    }
}
