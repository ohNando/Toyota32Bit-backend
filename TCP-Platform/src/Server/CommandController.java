package Server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Properties;

public class CommandController {
    private final Properties properties;
    private Set<String> activeSubs = new HashSet<>();
    CommandController(Properties properties){
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
        return receivedMessage.split("\\|")[0];
    }

    public String getPlatformName(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        if(parts.length > 1)
            return parts[1].split("_")[0];
        else
            return "";
    }

    public String getCurrencyRate(String receivedMessage){
        String[] parts = receivedMessage.split("\\|");
        if(parts.length > 1)
            return parts[1].split("_")[1];
        else
            return "";
    }

    String checkCommand(String receivedMessage){
        String commandName = getCommandName(receivedMessage);
        String platformName = getPlatformName(receivedMessage);
        String currencyRate = getCurrencyRate(receivedMessage);

        if(!isValidCommand(commandName)) return "(-)|Invalid command";
        if(!isValidPlatformName(platformName)) return "(-)|Invalid platform name";
        if(!isValidRate(currencyRate)) return "(-)|Invalid currency pair";
        return "OK";
    }
}
