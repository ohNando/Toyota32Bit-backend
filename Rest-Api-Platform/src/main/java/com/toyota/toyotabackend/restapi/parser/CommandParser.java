package com.toyota.toyotabackend.restapi.parser;

import com.toyota.toyotabackend.restapi.configuration.RateConfig;

import java.util.Properties;

/**
 * Implementation of the {@link CommandParser} interface that provides methods for parsing and validating
 * commands related to platform names and currency rates.
 * <p>
 * This class loads configuration properties from the application properties file to check if the received
 * platform and rate are valid.
 * </p>
 */
public class CommandParser {

    /**
     * Checks if the given platform name matches the platform name specified in the configuration properties.
     *
     * @param platformName the name of the platform to validate.
     * @return {@code true} if the platform name is valid, otherwise {@code false}.
     */
    public static boolean isValidPlatformName(String platformName) {
        Properties prop = RateConfig.loadProperties();
        String configPlatformName = prop.getProperty("command.platform-name");
        return configPlatformName != null && configPlatformName.equals(platformName);
    }

    /**
     * Extracts the platform name from the received message.
     *
     * @param receivedMessage the message from which to extract the platform name.
     * @return the platform name, or an empty string if not found.
     */

    public static String getPlatformName(String receivedMessage){
        String[] platformParts = receivedMessage.split("_");
        return platformParts.length > 0 ? platformParts[0] : "";
    }

    /**
     * Validates the received message by checking the platform name and currency rate.
     *
     * @param receivedMessage the message to validate.
     * @return a validation result true or false
     */
    public static boolean checkCommand(String receivedMessage) {
        if(receivedMessage.isEmpty()) return false;
        if(getPlatformName(receivedMessage).isEmpty()) return false;
        return isValidPlatformName(getPlatformName(receivedMessage));
    }
}
