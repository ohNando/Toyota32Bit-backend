package Server.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * A controller for parsing and validating commands received from clients.
 * It checks whether the command, platform name, and currency rate are valid
 * based on properties defined in the configuration.
 */
public class CommandController {
    private final Properties properties;

    /**
     * Constructs a CommandController with the given properties.
     *
     * @param properties The configuration properties used for validation.
     */
    public CommandController(Properties properties){
        this.properties = properties;
    }

    /**
     * Validates whether the provided currency rate is allowed.
     *
     * @param currencyRate The currency rate to validate.
     * @return {@code true} if the currency rate is allowed, {@code false} otherwise.
     */
    private boolean isValidRate(String currencyRate){
        String allowedRates = properties.getProperty("rates.allowed-rates");
        if(allowedRates != null){
            List<String> validRates = Arrays.asList(allowedRates.split(","));
            return validRates.contains(currencyRate);
        }
        return false;
    }

    /**
     * Validates whether the provided command is allowed.
     *
     * @param command The command to validate.
     * @return {@code true} if the command is allowed, {@code false} otherwise.
     */
    private boolean isValidCommand(String command){
        String allowedCommands = properties.getProperty("command.allowed-commands");
        if(allowedCommands != null){
            List<String> validCommands = Arrays.asList(allowedCommands.split(","));
            return validCommands.contains(command);
        }
        return false;
    }

    /**
     * Validates whether the provided platform name is correct.
     *
     * @param platformName The platform name to validate.
     * @return {@code true} if the platform name matches the expected value, {@code false} otherwise.
     */
    private boolean isValidPlatformName(String platformName){
        return platformName.equals(properties.getProperty("command.platform-name"));
    }

    /**
     * Extracts the command name from the received message.
     *
     * @param receivedMessage The message to extract the command name from.
     * @return The command name, or an empty string if the message format is incorrect.
     */
    public String getCommandName(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        return parts.length > 0 ? parts[0] : "";
    }

    /**
     * Extracts the platform name from the received message.
     *
     * @param receivedMessage The message to extract the platform name from.
     * @return The platform name, or an empty string if the message format is incorrect.
     */
    public String getPlatformName(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        if(parts.length > 1) {
            String[] platformParts = parts[1].split("_");
            return platformParts.length > 0 ? platformParts[0] : "";
        }
        return "";
    }

    /**
     * Extracts the currency rate from the received message.
     *
     * @param receivedMessage The message to extract the currency rate from.
     * @return The currency rate, or an empty string if the message format is incorrect.
     */
    public String getCurrencyRate(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        if(parts.length > 1) {
            String[] platformParts = parts[1].split("_");
            return platformParts.length > 1 ? platformParts[1] : "";
        }
        return "";
    }

    /**
     * Validates the command by checking the command name, platform name, and currency rate.
     *
     * @param receivedMessage The message containing the command to check.
     * @return true if all parts are valid, or false.
     */
    public Boolean checkCommand(String receivedMessage){
        String commandName = getCommandName(receivedMessage);
        String platformName = getPlatformName(receivedMessage);
        String currencyRate = getCurrencyRate(receivedMessage);

        if(commandName.isEmpty() || platformName.isEmpty() || currencyRate.isEmpty())
            return false;

        if(!isValidCommand(commandName) || !isValidPlatformName(platformName) || !isValidRate(currencyRate))
            return false;
        return true;
    }
}
