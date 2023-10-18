package common;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Properties;

@Getter
@Log4j2
@Singleton
public class ConfigurationXML {

    private static ConfigurationXML instance=null;
    private Properties p;
    public ConfigurationXML() {

        try {
            p = new Properties();
            p.loadFromXML(getClass().getClassLoader().getResourceAsStream("config.properties.xml"));

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static ConfigurationXML getInstance() {

        if (instance==null) {
            instance=new ConfigurationXML();
        }
        return instance;
    }

    public String getProperty(String key) {
        return p.getProperty(key);
    }
}
