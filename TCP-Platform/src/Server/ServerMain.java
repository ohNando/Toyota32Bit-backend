package Server;

import Server.Initializer.ServerInitializer;

/**
 * The main entry point for the server application.
 */
public class ServerMain {
    /**
     * The main method that starts the server.
     *
     * @param args Command line arguments (not used in this implementation).
     */
    public static void main(String[] args) {
        ServerInitializer init = new ServerInitializer();
        init.startServer();
    }
}