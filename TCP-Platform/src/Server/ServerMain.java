package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * The main entry point for the server application. This class reads server configurations
 * from a properties file, starts a server socket on a specified port, and continuously
 * listens for incoming client connections. Each client connection is handled in a separate thread.
 */
public class ServerMain {
    /**
     * The main method that starts the server.
     * <p>
     * This method loads the server configuration from a properties file, retrieves the port
     * number, and creates a `ServerSocket` to accept incoming client connections. For each connection,
     * a new `ServerHandler` thread is started to handle the client's requests.
     * </p>
     *
     * @param args Command line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        Properties properties = new Properties();
        String configFilePath = System.getProperty("user.dir") + "/src/config.properties";

        try (FileInputStream configFile = new FileInputStream(configFilePath)) {
            // Load properties from the configuration file
            properties.load(configFile);
            int port = Integer.parseInt(properties.getProperty("tcp-platform.server.port"));

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                // Continuously accept incoming client connections
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        new Thread(new ServerHandler(socket, properties)).start();
                    } catch (IOException error) {
                        System.out.println("(!)|Client cannot connect: " + error.getMessage());
                        break;
                    }
                }
            }
        } catch (IOException error) {
            // Handle errors in loading configuration or starting the server
            System.out.println("(!)|Server cannot start: " + error.getMessage());
            error.printStackTrace();
        }
    }
}
