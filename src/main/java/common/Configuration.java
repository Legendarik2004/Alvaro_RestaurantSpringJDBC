package common;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Getter
@Log4j2
@Singleton
public class Configuration {

    private static Configuration instance=null;
    private Properties p;
    public Configuration() {
        Path p1 = Paths.get("src/main/resources/mysql-properties.xml");
        p= new Properties();
        InputStream propertiesStream;
        try {
            propertiesStream = Files.newInputStream(p1);
            p.loadFromXML(propertiesStream);


        } catch (IOException e) {
            e.printStackTrace();
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
