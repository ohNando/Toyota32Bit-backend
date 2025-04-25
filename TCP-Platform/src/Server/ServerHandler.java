package Server;

import Server.Auth.LoginHandler;
import Server.Parser.CommandController;
import Server.Producer.RateDataProducer;

import java.io.*;
import java.net.*;
import java.util.Properties;

/**
 * A thread that handles communication with a client in the server.
 * This class processes client login, validates commands, and manages subscriptions to
 * currency rate data. It operates over a single socket connection with the client.
 */
public class ServerHandler extends Thread {
    private final Socket socket;
    private final Properties properties;
    private PrintWriter output;
    private RateDataProducer dataProducer;

    /**
     * Constructs a ServerHandler for handling a specific client connection.
     *
     * @param socket The socket representing the client connection.
     * @param properties The properties containing configuration settings for the server.
     */
    ServerHandler(Socket socket, Properties properties) {
        this.socket = socket;
        this.properties = properties;
    }

    /**
     * Runs the thread that handles client communication, including login, command processing,
     * and currency rate subscriptions/unsubscriptions.
     * <p>
     * The client is first prompted for login credentials, and if the authentication is successful,
     * commands are read and processed. The server subscribes to or unsubscribes from currency rate
     * data based on the received commands.
     * </p>
     */
    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.output = output;
            output.println("login|username|password");
            if (!LoginHandler.authenticate(input, output, properties)) {    // Login check
                socket.close();
                return;
            }

            CommandController commandController = new CommandController(properties);
            String receivedMessage;

            // Processing commands from the client
            while ((receivedMessage = input.readLine()) != null) {
                String checkedMessage = commandController.checkCommand(receivedMessage);

                if (!checkedMessage.equals("OK")) {
                    output.println(checkedMessage);  // Invalid command response
                } else {
                    String commandName = commandController.getCommandName(receivedMessage);
                    String currencyRate = commandController.getCurrencyRate(receivedMessage);

                    // Handling subscribe and unsubscribe commands
                    if (commandName.equals("subscribe")) {
                        dataProducer = new RateDataProducer(properties);
                        dataProducer.subscribe(currencyRate.trim(), output);
                    } else if (commandName.equals("unsubscribe")) {
                        if (dataProducer != null) {
                            dataProducer.unsubscribe(currencyRate.trim(), output);
                        }
                    }
                }
            }
        } catch (IOException error) {
            error.printStackTrace();  // Log the exception
        } finally {
            try {
                socket.close();  // Ensure the socket is closed in the end
            } catch (IOException error) {
                error.printStackTrace();  // Log the exception if closing fails
            }
        }
    }
}
