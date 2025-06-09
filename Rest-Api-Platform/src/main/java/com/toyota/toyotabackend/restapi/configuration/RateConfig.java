package com.toyota.toyotabackend.restapi.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility class to load and provide rate-related properties
 * from the 'application.properties' configuration file.
 */
public class RateConfig {
    private final static String configPath = "application.properties";

    /**
     * Loads properties from the 'application.properties' file located in the classpath.
     *
     * @return Properties object containing key-value pairs from the config file.
     * @throws RuntimeException if the resource file is not found or an IOException occurs.
     */
    public static Properties loadProperties() {
        Properties prop = new Properties();
        try(InputStream input = RateConfig.class.getClassLoader().getResourceAsStream(configPath)){
            if(input == null){
                throw new IOException("Resource not found: " + configPath);
            }
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }

    /**
     * Retrieves the list of allowed rate names configured in the properties file.
     * The property key is "rates.allowed-rates", and values are expected to be comma-separated.
     *
     * @return An array of allowed rate names as strings.
     */
    public static String[] getRateName() {
        Properties prop = loadProperties();
        return prop.getProperty("rates.allowed-rates").split(",");
    }

    /**
     * Retrieves the configured ask price for a given rate name.
     * Property key format: "rates.ask.{rateName}".
     *
     * @param rateName The name of the rate to retrieve the ask price for.
     * @return The ask price as a float.
     */
    public static float getRateAsk(String rateName) {
        Properties prop = loadProperties();
        return Float.parseFloat(prop.getProperty("rates.ask." + rateName));
    }

    /**
     * Retrieves the configured bid price for a given rate name.
     * Property key format: "rates.bid.{rateName}".
     *
     * @param rateName The name of the rate to retrieve the bid price for.
     * @return The bid price as a float.
     */
    public static float getRateBid(String rateName) {
        Properties prop = loadProperties();
        return Float.parseFloat(prop.getProperty("rates.bid." + rateName));
    }
}
