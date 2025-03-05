package Server;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CommandController {
    private final Properties properties;
    CommandController(Properties properties){
        this.properties = properties;
    }

    boolean isValidRate(String currencyPair){
        String allowedRates = properties.getProperty("rates.allowed-rates");
        if(allowedRates != null){
            List<String> validRates = Arrays.asList(allowedRates.split(","));
            return validRates.contains(currencyPair);
        }
        return false;
    }

    //check command komtuu yaz tum gelen istegi kontrol etsin!!

}
