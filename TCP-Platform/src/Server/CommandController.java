package Server;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CommandController {
    private final Properties properties;
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

    String checkCommand(String receivedMessage){
        String commandName = receivedMessage.split("\\|")[0];
        String platformName = receivedMessage.split("\\|")[1].split("_")[0];
        String currencyRate = receivedMessage.split("\\|")[1].split("_")[1];

        if(!isValidCommand(commandName)) return "(-)|Invalid command";
        if(!isValidPlatformName(platformName)) return "(-)|Invalid platform name";
        if(!isValidRate(currencyRate)) return "(-)|Invalid currency pair";
        return "OK";
    }

}
