package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * RateConfig is a utility class for loading and accessing
 * rate-related configuration properties from a properties file.
 * The configuration file path is fixed as "config.properties" in the classpath.
 * It provides methods to retrieve allowed rate names, as well as bid and ask values
 * for specific rates.
 */
public class RateConfig {
    private final static String configPath = "config.properties";

    /**
     * Loads the properties file from the classpath.
     *
     * @return Properties object loaded from the file
     * @throws RuntimeException if the properties file cannot be found or loaded
     */
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

    /**
     * Retrieves the list of allowed rate names defined in the configuration.
     * The rates are expected to be defined as a comma-separated string under the key
     * "rates.allowed-rates".
     *
     * @return an array of rate names
     */
    public static String[] getRateNames() {
        Properties prop = loadProperties();
        return prop.getProperty("rates.allowed-rates").split(",");
    }

    /**
     * Retrieves the bid price for the specified rate name from the configuration.
     * The bid price is expected under the key pattern "rates.bid.<rateName>".
     *
     * @param rateName the name of the rate whose bid price is requested
     * @return the bid price as a float
     */
    public static float getRateBid(String rateName) {
        Properties prop = loadProperties();

        String bid = prop.getProperty("rates.bid." + rateName);
        return Float.parseFloat(bid);
    }

    /**
     * Retrieves the ask price for the specified rate name from the configuration.
     * The ask price is expected under the key pattern "rates.ask.<rateName>".
     *
     * @param rateName the name of the rate whose ask price is requested
     * @return the ask price as a float
     */
    public static float getRateAsk(String rateName) {
        Properties prop = loadProperties();
        String ask = prop.getProperty("rates.ask." + rateName);
        return Float.parseFloat(ask);
    }
}
