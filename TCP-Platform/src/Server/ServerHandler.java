package Server;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Properties;

public class ServerHandler implements Runnable{
    private final Socket socket;
    private final Properties properties;

    ServerHandler(Socket socket, Properties properties) {
        this.socket = socket;
        this.properties = properties;
    }

    @Override
    public void run(){
        try(BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true)
        ){
            CommandController commandController = new CommandController(properties);
            String receivedMessage = input.readLine();
            String checkedMessage = commandController.checkCommand(receivedMessage);
            if(!Objects.equals(checkedMessage, "OK")){
                output.println(checkedMessage);
            }else{
                output.println("(+)|subscribed to "+ receivedMessage.split("\\|")[1]);
                String currencyRate = receivedMessage.split("\\|")[1].split("_")[1];
                RateDataProducer dataProducer = new RateDataProducer(properties);
                String finalMessage = dataProducer.generateRate(currencyRate);
                output.println(finalMessage);
            }
        }catch (IOException error){
            error.printStackTrace();
        }finally{
            try{
                socket.close();
            }catch (IOException error){
                error.printStackTrace();
            }
        }
    }


}
