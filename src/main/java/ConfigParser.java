import exception.EmptyKeyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigParser {
    private static final String FILE_NAME = "config.properties";
    private static final String DEFAULT_LANGUAGE = "ru";
    private String key;
    private String destLang;
    private ConfigParser configParser;

    private ConfigParser() {
        try {
            loadProperties();
        } catch (EmptyKeyException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public ConfigParser getConfigParser() {
        if (configParser == null) {
            configParser = new ConfigParser();
        }
        return configParser;
    }

    public void loadProperties() {
        Properties appProps = getProperties();
        destLang = appProps.getProperty("destination_language", DEFAULT_LANGUAGE);
        key = appProps.getProperty("key");
        if (key == null) {
            throw new EmptyKeyException();
        }
    }

    public String getKey() {
        return key;
    }

    public String getDefaultLang() {
        return destLang;
    }

    private Properties getProperties() {
        String configPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + FILE_NAME;
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configPath));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return appProps;
    }
}
