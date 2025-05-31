package com.toyota.toyotabackend.restapi.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RateConfig {
    private final static String configPath = "application.properties";

    public static Properties loadProperties() {
        Properties prop = new Properties();
        try(InputStream input = RateConfig.class.getClassLoader().getResourceAsStream(configPath)){
            if(input == null){
                System.err.println("Resource not found: " + configPath);
            }
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }

    public static String[] getRateName() {
        Properties prop = loadProperties();
        return prop.getProperty("rates.allowed-rates").split(",");
    }

    public static float getRateAsk(String rateName) {
        Properties prop = loadProperties();
        return Float.parseFloat(prop.getProperty("rates.ask." + rateName));
    }

    public static float getRateBid(String rateName) {
        Properties prop = loadProperties();
        return Float.parseFloat(prop.getProperty("rates.bid." + rateName));
    }
}
