package Server;

import Server.Auth.LoginHandler;
import Server.Parser.CommandController;
import Server.Producer.RateDataProducer;

import java.io.*;
import java.net.*;
import java.util.Properties;

public class ServerHandler extends Thread {
    private final Socket socket;
    private final Properties properties;
    private PrintWriter output;
    private RateDataProducer dataProducer;

    ServerHandler(Socket socket, Properties properties) {
        this.socket = socket;
        this.properties = properties;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.output = output;
            output.println("login|username|password");
            if(!LoginHandler.authenticate(input,output,properties)) {    //Login
                socket.close();
                return;
            }

            CommandController commandController = new CommandController(properties);
            String receivedMessage;

            while ((receivedMessage = input.readLine()) != null) {
                String checkedMessage = commandController.checkCommand(receivedMessage);

                if (!checkedMessage.equals("OK")) {
                    output.println(checkedMessage);
                } else {
                    String commandName = commandController.getCommandName(receivedMessage);
                    String currencyRate = commandController.getCurrencyRate(receivedMessage);

                    if (commandName.equals("subscribe")) {
                        dataProducer = new RateDataProducer(properties);
                        dataProducer.subscribe(currencyRate.trim(), output);
                    } else if(commandName.equals("unsubscribe")) {
                        if (dataProducer != null) {
                            dataProducer.unsubscribe(currencyRate.trim(),output);
                        }
                    }
                }
            }
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }
}
