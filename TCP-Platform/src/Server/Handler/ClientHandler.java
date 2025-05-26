package Server.Handler;

import Server.Auth.LoginHandler;
import Server.Parser.CommandController;
import Server.Producer.RateDataProducer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

/**
 * A thread that handles communication with a client in the server.
 * This class processes client login, validates commands, and manages subscriptions to
 * currency rate data. It operates over a single socket connection with the client.
 */
public class ClientHandler extends Thread {
    private final Socket socket;
    private final Properties properties;
    private RateDataProducer dataProducer;
    private PrintWriter output;
    private BufferedReader input;

    /**
     * Constructs a ServerHandler for handling a specific client connection.
     *
     * @param socket The socket representing the client connection.
     * @param properties The properties containing configuration settings for the server.
     */
    public ClientHandler(Socket socket, Properties properties) {
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
            this.input = input;

            if (!LoginHandler.authenticate(input, output, properties)) {    // Login check
                socket.close();
                return;
            }

            CommandController commandController = new CommandController(properties);
            String receivedMessage;

            // Processing commands from the client
            while ((receivedMessage = input.readLine()) != null) {
                Boolean checkedMessage = commandController.checkCommand(receivedMessage);
                
                if(checkedMessage){
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
