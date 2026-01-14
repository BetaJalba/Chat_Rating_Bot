import java.sql.*;
import java.util.Objects;
import java.util.StringTokenizer;

public class DataService {
    private static DataService instance;
    private final Connection connection;

    private DataService() throws SQLException {
        String url = "jdbc:sqlite:database/data.db";
        connection = DriverManager.getConnection(url);
        System.out.println("Connected to database");
    }

    public static DataService getInstance() throws SQLException {
        if (instance == null)
            instance = new DataService();
        return instance;
    }

    private String executeQuery(String query, Object... params) {
        try {
            if(connection == null || !connection.isValid(5)){
                System.err.println("Errore di connessione al database");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Errore di connessione al database");
            return null;
        }

        try (PreparedStatement stmt = connection.prepareStatement(query);) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            StringBuilder result = new StringBuilder();
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(rs.getObject(i));
                        if (i < columnCount)
                            result.append(", ");
                        else
                            result.append("\n");
                    }
                }
                return result.toString().isEmpty() ? null : result.toString();
            }
        } catch (SQLException e) {
            System.err.println("Errore di query: " + e.getMessage());
            return null;
        }
    }

    private int executeUpdate(String update, Object... params) {
        try {
            if(connection == null || !connection.isValid(5)){
                System.err.println("Errore di connessione al database");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Errore di connessione al database");
            return -1;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(update);

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore di query: " + e.getMessage());
            return -1;
        }
    }

    public int insertUser(long userId, String name) {
        String query = "INSERT INTO user(id, name) VALUES (?, ?)";

        return executeUpdate(query, userId, name);
    }

    public int insertChat(long chatId) {
        String query = "INSERT INTO chat(id) VALUES (?)";

        return executeUpdate(query, chatId);
    }

    public int incrementUserMessages(long userId, long chatId) {
        String query = "UPDATE participates SET message_count = (message_count + 1) WHERE user_id = ? AND chat_id = ?";

        return executeUpdate(query, userId, chatId);
    }

    public void addParticipation(long userId, long chatId) {
        String query = "INSERT INTO participates (user_id, chat_id) VALUES (?, ?)";
        executeUpdate(query, userId, chatId);
    }

    public boolean checkChat(long chatId){
        String query = "SELECT * FROM chat WHERE id = ?";
        return executeQuery(query, chatId) != null;
    }

    public boolean checkUser(long userId) {
        String query = "SELECT * FROM user WHERE id = ?";
        return executeQuery(query, userId) != null;
    }

    public double getUserScore(long userId, long chatId) {
        String query = "SELECT behavior_score FROM participates WHERE user_id = ? AND chat_id = ?";
        return Float.parseFloat(executeQuery(query, userId, chatId));
    }

    public void updateUserScore(long userId, long chatId, double score) {
        String query = "UPDATE participates SET behavior_score = ? WHERE user_id = ? AND chat_id = ?";
        executeUpdate(query, score, userId, chatId);
    }

    public double getMessageCountUserInChat(long userId, long chatId) {
        String query = "SELECT message_count FROM participates WHERE user_id = ? AND chat_id = ?";
        return Double.parseDouble(executeQuery(query, userId, chatId));
    }

    public String getChatLeaderboard(long chatId) {
        String query = """
                SELECT
                    u.name,
                    p.behavior_score
                FROM participates p
                JOIN user u ON u.id = p.user_id
                WHERE p.chat_id = ?
                ORDER BY p.behavior_score DESC;
                """;

        return executeQuery(query, chatId);
    }

    // Coalesce ritorna il primo campo non null, in questo caso lo usiamo per impostare una default value
    public long getTotalMessagesUser(long userId) {
        String query = "SELECT COALESCE(SUM(message_count), 0) FROM participates WHERE user_id = ?";
        return Long.parseLong(executeQuery(query, userId).trim());
    }

    public double getMeanBehaviorScoreUser(long userId) {
        String query = "SELECT COALESCE(AVG(behavior_score), 1.0) FROM participates WHERE user_id = ?";
        return Double.parseDouble(executeQuery(query, userId));
    }

    public double getMeanBehaviorScoreAllChats() {
        String query = "SELECT COALESCE(AVG(behavior_score), 1.0) FROM participates";
        return Double.parseDouble(executeQuery(query));
    }

    public boolean checkUserInChat(long userId, long chatId) {
        String query = "SELECT * FROM participates WHERE user_id = ? AND chat_id = ?";
        return executeQuery(query, userId, chatId) != null;
    }
}
