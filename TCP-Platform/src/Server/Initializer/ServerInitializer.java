package Server.Initializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import Server.Handler.ClientHandler;

/**
 * This class initializes and starts the server.
 * It reads configurations, creates a ServerSocket, and listens for incoming client connections.
 */
public class ServerInitializer {
    private final Properties properties;

    public ServerInitializer() {
        this.properties = loadProperties();
    }

    private Properties loadProperties(){
        Properties properties = new Properties();
        String configPath = System.getProperty("user.dir") + "/src/config.properties";
        try{
            FileInputStream configFile = new FileInputStream(configPath);
            properties.load(configFile);
        }catch(IOException error){
            System.out.println("(!)|Error loading properties: " + error.getMessage());
            error.printStackTrace();
        }
        return properties;
    }

    public void startServer() {
        try {
            int port = Integer.parseInt(properties.getProperty("tcp-platform.server.port"));
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is running on port " + port);

                // Continuously accept incoming client connections
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        new Thread(new ClientHandler(socket, properties)).start();
                    } catch (IOException error) {
                        System.out.println("(!)|Client cannot connect: " + error.getMessage());
                        break;
                    }
                }
            }
        } catch (IOException error) {
            System.out.println("(!)|Server cannot start: " + error.getMessage());
            error.printStackTrace();
        }
    }
}
