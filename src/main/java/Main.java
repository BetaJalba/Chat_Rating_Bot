import com.google.gson.Gson;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();
        Configurations configs = new Configurations();
        Configuration configuration = null;
        try {
            configuration = configs.properties("config.properties");
        } catch (ConfigurationException e) {
            System.err.println("File config non esiste!");
            System.exit(-1);
        }

    }
}
