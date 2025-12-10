import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationService {
    private static ConfigurationService instance;
    private Configuration configuration = null;

    private ConfigurationService() {
        try {
            Configurations configs = new Configurations();
            configuration = configs.properties("config.properties");
        } catch (ConfigurationException e) {
            System.err.println("File config non esiste!");
        }
    }

    public static ConfigurationService getInstance() {
        if (instance == null)
            instance = new ConfigurationService();
        return instance;
    }

    public String getProperty(String propertyName){
        return configuration.getProperty(propertyName).toString();
    }
}

