import Server.Server;

import java.io.IOException;

/**
 * The main entry point for the server application.
 */
public class ServerMain {
    /**
     * The main method that starts the server.
     *
     * @param args Command line arguments (not used in this implementation).
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}