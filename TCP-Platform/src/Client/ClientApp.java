package Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClientApp {
    public static void main(String[] args) {
        try{
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/config.properties"));
            String serverAddress = properties.getProperty("tcp-platform.server.server-address");
            int port = Integer.parseInt(properties.getProperty("tcp-platform.server.port"));

            ClientHandler clientHandler = new ClientHandler(serverAddress,port);
            clientHandler.sendRequest("EURUSD");
        }catch(IOException error){
            error.printStackTrace();
        }
    }
}
