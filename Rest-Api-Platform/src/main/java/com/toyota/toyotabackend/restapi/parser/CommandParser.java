package com.toyota.toyotabackend.restapi.parser;

public interface CommandParser {
    boolean isValidRate(String rateName);
    boolean isValidPlatformName(String platformName);

    String getRate(String receivedMessage);
    String getPlatformName(String receivedMessage);
    String checkCommand(String receivedMessage);
}
