package org.example;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private final static Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key){
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("database.properties")){
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
