package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RateConfig {
    private final static String configPath = "config.properties";

    private static Properties loadProperties() {
        Properties prop = new Properties();
        try(InputStream in = RateConfig.class.getClassLoader().getResourceAsStream(configPath)) {
            if(in == null) {
                System.err.println("Resource not found: " + configPath);
            }
            prop.load(in);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return prop;
    }

    public static String[] getRateNames() {
        Properties prop = loadProperties();
        return prop.getProperty("rates.allowed-rates").split(",");
    }

    public static float getRateBid(String rateName) {
        Properties prop = loadProperties();

        String bid = prop.getProperty("rates.bid." + rateName);
        return Float.parseFloat(bid);
    }

    public static float getRateAsk(String rateName) {
        Properties prop = loadProperties();
        String ask = prop.getProperty("rates.ask." + rateName);
        return Float.parseFloat(ask);
    }
}
