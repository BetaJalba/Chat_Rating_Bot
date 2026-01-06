import java.sql.*;
import java.util.Objects;

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
                            result.append("\t");
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

    public String getUsers() {
        String query = "SELECT * FROM user";

        return executeQuery(query);
    }

    public int insertUser(long userId) {
        String query = "INSERT INTO user(id) VALUES (?)";

        return executeUpdate(query, userId);
    }

    public int incrementUserMessages(long userId) {
        String query = "UPDATE user SET message_count = (message_count + 1) WHERE id = ?";

        return executeUpdate(query, userId);
    }

    public boolean checkUser(long userId) {
        String query = "SELECT * FROM user WHERE id = ?";
        return executeQuery(query, userId) != null;
    }
}
