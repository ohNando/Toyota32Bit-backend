package Server;

import java.io.*;
import java.net.*;
import java.util.Properties;

public class ServerHandler implements Runnable{
    private final Socket socket;
    private final Properties properties;
    private PrintWriter output;
    private RateDataProducer dataProducer;

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
            this.output = output;
            String receivedMessage;

            while((receivedMessage = input.readLine()) != null){
                String checkedMessage = commandController.checkCommand(receivedMessage);

                if(!checkedMessage.equals("OK")){
                    output.println(checkedMessage);
                }else{
                    String commandName = commandController.getCommandName(receivedMessage);
                    String currencyRate = commandController.getCurrencyRate(receivedMessage);
                    if(commandName.equals("subscribe")){
                        output.println("(+)|subscribed to " + currencyRate);
                        dataProducer = new RateDataProducer(properties);
                        new Thread(()->dataProducer.subscribe(currencyRate,output)).start();
                    }
                    else{
                        output.println("(+)|unsubscribed from " + currencyRate);
                        if(dataProducer != null){
                            dataProducer.unsubscribe(currencyRate);
                        }
                    }
                }
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
