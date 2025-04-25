package com.toyota.toyotabackend.restapi.parser.impl;

import com.toyota.toyotabackend.restapi.parser.CommandParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of the {@link CommandParser} interface that provides methods for parsing and validating
 * commands related to platform names and currency rates.
 * <p>
 * This class loads configuration properties from the application properties file to check if the received
 * platform and rate are valid.
 * </p>
 */
@Component
public class CommandParserImpl implements CommandParser {
    private final Properties properties;

    /**
     * Constructs a new CommandParserImpl, loading properties from the "application.properties" file
     * located in the classpath.
     */
    public CommandParserImpl() {
        properties = new Properties();
        try(InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if(input != null) {
                properties.load(input);
            }
        }
        catch (IOException error){
            error.printStackTrace();
        }
    }

    /**
     * Checks if the given currency rate is valid by comparing it against a list of allowed rates from the properties.
     *
     * @param rateName the name of the currency rate to validate.
     * @return {@code true} if the rate is valid, otherwise {@code false}.
     */
    @Override
    public boolean isValidRate(String rateName) {
        String allowedRates = properties.getProperty("rates.allowed-rates");
        if(allowedRates != null){
            List<String> validRates = Arrays.asList(allowedRates.split(","));
            return validRates.contains(rateName);
        }
        return false;
    }

    /**
     * Checks if the given platform name matches the platform name specified in the configuration properties.
     *
     * @param platformName the name of the platform to validate.
     * @return {@code true} if the platform name is valid, otherwise {@code false}.
     */
    @Override
    public boolean isValidPlatformName(String platformName) {
        String configPlatformName = properties.getProperty("command.platform-name");
        return configPlatformName != null && configPlatformName.equals(platformName);
    }

    /**
     * Extracts the currency rate from the received message.
     *
     * @param receivedMessage the message from which to extract the currency rate.
     * @return the currency rate, or an empty string if not found.
     */
    @Override
    public String getRate(String receivedMessage) {
        String[] platformParts = receivedMessage.split("_");
        return platformParts.length > 1 ? platformParts[1] : "";
    }

    /**
     * Extracts the platform name from the received message.
     *
     * @param receivedMessage the message from which to extract the platform name.
     * @return the platform name, or an empty string if not found.
     */
    @Override
    public String getPlatformName(String receivedMessage){
        String[] platformParts = receivedMessage.split("_");
        return platformParts.length > 0 ? platformParts[0] : "";
    }

    /**
     * Validates the received message by checking the platform name and currency rate.
     *
     * @param receivedMessage the message to validate.
     * @return a validation result message indicating success or the type of error.
     */
    @Override
    public String checkCommand(String receivedMessage) {
        String platformName = getPlatformName(receivedMessage);
        String currencyRate = getRate(receivedMessage);

        if(platformName.isEmpty()) return "(-)|Missing platform name";
        if(currencyRate.isEmpty()) return "(-)|Missing currency rate";

        if(!isValidPlatformName(platformName)) return "(-)|Invalid platform name";
        if(!isValidRate(currencyRate)) return "(-)|Invalid currency rate";

        return "OK";
    }
}
