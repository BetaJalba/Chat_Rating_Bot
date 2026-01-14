import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class BehaviorRater {
    DataService dataService = DataService.getInstance();
    private final ConfigurationService configurationService = ConfigurationService.getInstance();
    private final String url = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key="+ configurationService.getProperty("PERSPECTIVE_API_KEY");

    public BehaviorRater() throws SQLException {

    }

    public void rateMessage(String message, long userId, long chatId) {
        MediaType mediaType = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();

        StringBuilder requestedAttributesFormatted = new StringBuilder();
        StringTokenizer requestedAttributes = new StringTokenizer(configurationService.getProperty("RATE_MESSAGE") != null ? configurationService.getProperty("RATE_MESSAGE").strip() : "en", ",");
        while (requestedAttributes.hasMoreTokens()) {
            String attr = requestedAttributes.nextToken().strip();
            requestedAttributesFormatted.append("\"").append(attr).append("\": {}");
            if (requestedAttributes.hasMoreTokens()) {
                requestedAttributesFormatted.append(", ");
            }
        }

        StringBuilder languagesFormatted = new StringBuilder();
        StringTokenizer languages = new StringTokenizer(configurationService.getProperty("MESSAGE_LANGUAGES").strip(), ",");
        while (languages.hasMoreTokens()) {
            languagesFormatted.append("\"").append(languages.nextToken().strip()).append("\"");
            if (languages.hasMoreTokens()) {
                languagesFormatted.append(", ");
            }
        }

        String bodyJson = "{"
                + "\"comment\": { \"text\": \"" + message.replace("\"", "\\\"") + "\" },"
                + "\"languages\": [" + languagesFormatted + "],"
                + "\"requestedAttributes\": {" + requestedAttributesFormatted + "}"
                + "}";

        RequestBody body = RequestBody.create(bodyJson, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("Unexpected code " + response);

            updateRating(response.body().string(), userId, chatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateRating(String responseBody, long userId, long chatId) throws JsonProcessingException {
        System.out.println(userId + "'s response body in chat " + chatId + ": \n" + responseBody);

        String rateMessageConfig = configurationService.getProperty("RATE_MESSAGE") != null ? configurationService.getProperty("RATE_MESSAGE") : "TOXICITY";
        StringTokenizer attributeTokenizer = new StringTokenizer(rateMessageConfig.strip(), ",");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);
        JsonNode attributeScores = root.path("attributeScores");

        double meanValue = 0.0;
        int i = 0;

        // Loop through attributes from StringTokenizer
        while (attributeTokenizer.hasMoreTokens()) {
            String attr = attributeTokenizer.nextToken().strip();
            JsonNode valueNode = attributeScores.path(attr).path("summaryScore").path("value");
            double value = valueNode.asDouble(0.0); // default 0
            meanValue += value;
            i++;
        }
        meanValue /= i;

        // 1 = polite, 0 = very toxic
        double behaviorScore = 1.0 - meanValue;
        behaviorScore = Math.max(0, Math.min(1, behaviorScore)); // clamp 0–1

        double biasConfig = configurationService.getProperty("MESSAGE_BIAS") != null ? Double.parseDouble(configurationService.getProperty("MESSAGE_BIAS")) : 0.1;
        double messageBias = biasConfig/dataService.getMessageCountUserInChat(userId, chatId);

        double alpha = configurationService.getProperty("SCORE_ALPHA") != null ? Double.parseDouble(configurationService.getProperty("SCORE_ALPHA")) : 0.1; // smoothing factor
        double prevScore = dataService.getUserScore(userId, chatId);
        double newUserScore = prevScore * (1 - alpha - messageBias) + behaviorScore * (alpha + messageBias);

        dataService.updateUserScore(userId, chatId, newUserScore);
    }
}
