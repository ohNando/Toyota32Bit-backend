package Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
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

    public static int getServerPort(){
        Properties prop = loadProperties();
        return Integer.parseInt(prop.getProperty("tcp-platform.server.port"));
    }
}
