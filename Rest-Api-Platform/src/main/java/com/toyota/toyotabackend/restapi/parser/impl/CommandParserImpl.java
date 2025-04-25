package com.toyota.toyotabackend.restapi.parser.impl;

import com.toyota.toyotabackend.restapi.parser.CommandParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class CommandParserImpl implements CommandParser {
    private final Properties properties;

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

    @Override
    public boolean isValidRate(String rateName) {
        String allowedRates = properties.getProperty("rates.allowed-rates");
        if(allowedRates != null){
            List<String> validRates = Arrays.asList(allowedRates.split(","));
            return validRates.contains(rateName);
        }
        return false;
    }

    @Override
    public boolean isValidPlatformName(String platformName) {
        String configPlatformName = properties.getProperty("command.platform-name");
        return configPlatformName != null && configPlatformName.equals(platformName);
    }

    @Override
    public String getRate(String receivedMessage) {
        String[] platformParts = receivedMessage.split("_");
        return platformParts.length > 1 ? platformParts[1] : "";
    }

    @Override
    public String getPlatformName(String receivedMessage){
        String[] platformParts = receivedMessage.split("_");
        return platformParts.length > 0 ? platformParts[0] : "";
    }

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
