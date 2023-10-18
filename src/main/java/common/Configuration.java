package common;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Properties;

@Getter
@Log4j2
@Singleton
public class Configuration {

    private String pathDatosCustomers;
    private String pathDatosOrdersCsv;
    private static Configuration instance=null;
    private Properties p;
    public Configuration() {

        try {
            p = new Properties();
            p.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            this.pathDatosCustomers = p.getProperty("pathDataCustomers");
            this.pathDatosOrdersCsv = p.getProperty("pathDataOrdersCsv");


        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Configuration getInstance() {

        if (instance==null) {
            instance=new Configuration();
        }
        return instance;
    }

    public String getProperty(String key) {
        return p.getProperty(key);
    }
}
