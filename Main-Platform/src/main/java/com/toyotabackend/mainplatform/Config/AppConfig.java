package com.toyotabackend.mainplatform.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.annotation.Configuration;

/**
 * The AppConfig class is a utility class used to load configuration values from the application properties file
 * (application.properties) and provide various configuration values that will be used throughout the application.
 * <p>
 * This class loads and provides values such as subscriber names, server addresses, port numbers, and platform-specific
 * class paths, among others.
 * </p>
 */
public class AppConfig {
    
    private static String propertiesPath = "application.properties"; // Path to the properties file

    /**
     * Returns the path to the properties file.
     *
     * @return propertiesPath The path to the properties file
     */
    String getPath(){ return propertiesPath; }

    /**
     * Loads the properties file and returns a Properties object containing all the configuration key-value pairs.
     * <p>
     * This method reads the application.properties file and loads it into a Properties object.
     * </p>
     *
     * @return The loaded properties
     * @throws IOException If the properties file cannot be found or read
     */
    private static Properties loadProperties() throws IOException {
        Properties prop = new Properties();
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream(propertiesPath)) {
            if (input == null) {
                throw new IOException("Property file not found in classpath.");
            }
            prop.load(input);
        }
        return prop;
    }

    /**
     * Returns the list of subscriber names defined in the properties file.
     *
     * @return An array of subscriber names
     * @throws IOException If there is an issue reading the properties file
     */
    public static String[] getSubscriberNames() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("subscriber.names").split(",");
    }

    /**
     * Returns the list of raw rates defined in the properties file.
     *
     * @return An array of raw rates
     * @throws IOException If there is an issue reading the properties file
     */
    public static String[] getRawRates() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("rate.rawRates").split(",");
    }

    /**
     * Returns the list of derived rates defined in the properties file.
     *
     * @return An array of derived rates
     * @throws IOException If there is an issue reading the properties file
     */
    public static String[] getDerivedRates() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("rate.derivedRates").split(",");
    }

    /**
     * Returns an array of calculated rates, which includes both raw and derived rates.
     * <p>
     * This method combines the raw rates and derived rates into a single array of calculated rates.
     * </p>
     *
     * @return An array of calculated rates
     * @throws IOException If there is an issue reading the properties file
     */
    public static String[] getCalculatedRates() throws IOException {
        String[] rawRates = getRawRates();
        String[] derivedRates = getDerivedRates();

        String[] calculatedRates = new String[rawRates.length + derivedRates.length];

        for (int i = 0; i < derivedRates.length; i++) {
            calculatedRates[i] = derivedRates[i];
        }

        for (int i = 0; i < rawRates.length; i++) {
            calculatedRates[i + derivedRates.length] = rawRates[i];
        }

        return calculatedRates;
    }

    /**
     * Returns the TCP server address defined in the properties file.
     *
     * @return The TCP server address
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getTCPAddress() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("client.TCP.serverAddress");
    }

    /**
     * Returns the TCP port number defined in the properties file.
     *
     * @return The TCP port number
     * @throws IOException If there is an issue reading the properties file
     */
    public static int getTCPPort() throws IOException {
        Properties prop = loadProperties();
        return Integer.parseInt(prop.getProperty("client.TCP.port"));
    }

    /**
     * Returns the TCP subscriber name defined in the properties file.
     *
     * @return The TCP subscriber name
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getTCPSubscriberName() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("subscriber.names.TCP");
    }

    /**
     * Returns the base URL for the REST API defined in the properties file.
     *
     * @return The base URL for the REST API
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getRESTBaseUrl() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("client.Rest_Api.baseUrl");
    }

    /**
     * Returns the login URL for the REST API defined in the properties file.
     *
     * @return The login URL for the REST API
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getRESTLoginUrl() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("client.Rest_Api.loginUrl");
    }

    /**
     * Returns the REST subscriber name defined in the properties file.
     *
     * @return The REST subscriber name
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getRESTSubscriberName() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("subscriber.names.REST");
    }

    /**
     * Returns the username for login defined in the properties file.
     *
     * @return The login username
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getUsername() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("login.username");
    }

    /**
     * Returns the password for login defined in the properties file.
     *
     * @return The login password
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getPassword() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("login.password");
    }

    /**
     * Returns the class path for the TCP subscriber defined in the properties file.
     *
     * @return The class path for the TCP subscriber
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getTCPClassPath() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("subscriber1.class");
    }

    /**
     * Returns the class path for the REST subscriber defined in the properties file.
     *
     * @return The class path for the REST subscriber
     * @throws IOException If there is an issue reading the properties file
     */
    public static String getRESTClassPath() throws IOException {
        Properties prop = loadProperties();
        return prop.getProperty("subscriber2.class");
    }
}
