package com.toyota.toyotabackend.restapi.parser;

/**
 * Interface for parsing and validating commands related to platform names and currency rates.
 * <p>
 * Implementations of this interface are responsible for extracting specific information from a message,
 * such as the platform name and currency rate, and checking whether they are valid according to configured rules.
 * </p>
 */
public interface CommandParser {
    /**
     * Validates if the given platform name matches the expected platform name.
     *
     * @param platformName the platform name to validate.
     * @return {@code true} if the platform name is valid, otherwise {@code false}.
     */
    boolean isValidPlatformName(String platformName);

    /**
     * Extracts the platform name from the received message.
     *
     * @param receivedMessage the message from which to extract the platform name.
     * @return the extracted platform name, or an empty string if not found.
     */
    String getPlatformName(String receivedMessage);

    /**
     * Validates the received message by checking its platform name and currency rate.
     *
     * @param receivedMessage the message to validate.
     * @return a validation result message indicating success or failure, including details on the validation error.
     */
    boolean checkCommand(String receivedMessage);
}
