import exception.EmptyKeyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigParser {
    private static final String FILE_NAME = "config.properties";
    private static final String DEFAULT_LANGUAGE = "en-ru";
    private String key;
    private String destLang;
    private static ConfigParser configParser;

    private ConfigParser() {
        try {
            loadProperties();
        } catch (EmptyKeyException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static ConfigParser getConfigParser() {
        if (configParser == null) {
            configParser = new ConfigParser();
        }
        return configParser;
    }

    public void loadProperties() {
        Properties appProps = getProperties();
        destLang = appProps.getProperty("from_to_lang", DEFAULT_LANGUAGE);
        key = appProps.getProperty("key");
        if (key == null) {
            throw new EmptyKeyException();
        }
    }

    public String getKey() {
        return key;
    }

    public String getDestinationLang() {
        return destLang;
    }

    private Properties getProperties() {
        String configPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + FILE_NAME;
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configPath));
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
        return appProps;
    }
}
