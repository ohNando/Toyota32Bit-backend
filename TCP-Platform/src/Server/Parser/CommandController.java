package Server.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CommandController {
    private final Properties properties;

    public CommandController(Properties properties){
        this.properties = properties;
    }

    private boolean isValidRate(String currencyRate){
        String allowedRates = properties.getProperty("rates.allowed-rates");
        if(allowedRates != null){
            List<String> validRates = Arrays.asList(allowedRates.split(","));
            return validRates.contains(currencyRate);
        }
        return false;
    }

    private boolean isValidCommand(String command){
        String allowedCommands = properties.getProperty("command.allowed-commands");
        if(allowedCommands != null){
            List<String> validCommands = Arrays.asList(allowedCommands.split(","));
            return validCommands.contains(command);
        }
        return false;
    }

    private boolean isValidPlatformName(String platformName){

        return platformName.equals(properties.getProperty("command.platform-name"));
    }

    public String getCommandName(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        return parts.length > 0 ? parts[0] : "";
    }

    public String getPlatformName(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        if(parts.length > 1) {
            String[] platformParts = parts[1].split("_");
            return platformParts.length > 0 ? platformParts[0] : "";
        }
        return "";
    }

    public String getCurrencyRate(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        if(parts.length > 1) {
            String[] platformParts = parts[1].split("_");
            return platformParts.length > 1 ? platformParts[1] : "";
        }
        return "";
    }

    public String checkCommand(String receivedMessage){
        String commandName = getCommandName(receivedMessage);
        String platformName = getPlatformName(receivedMessage);
        String currencyRate = getCurrencyRate(receivedMessage);

        if(commandName.isEmpty()) return "(-)|Missing command";
        if(platformName.isEmpty()) return "(-)|Missing platform name";
        if(currencyRate.isEmpty()) return "(-)|Missing currency rate";

        if(!isValidCommand(commandName)) return "(-)|Invalid command";
        if(!isValidPlatformName(platformName)) return "(-)|Invalid platform name";
        if(!isValidRate(currencyRate)) return "(-)|Invalid currency pair";
        return "OK";
    }
}
