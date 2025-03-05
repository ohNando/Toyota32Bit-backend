package Server;

import java.io.*;
import java.net.*;
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
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
            CommandController commandController = new CommandController(properties);
            //Tum gelen istegi kotnrol/parse edecek commandCOntrolleri bitir
        ){
            String acceptedMessage = input.readLine();
            if(acceptedMessage.startsWith("subscribe|PF1_")){
                output.println("(+)|subscribed to "+acceptedMessage.split("\\|")[1]);

                String currencyRate = acceptedMessage.split("\\|")[1].split("_")[1];
                if(commandController.isValidRate(currencyRate)){
                    RateDataProducer dataProducer = new RateDataProducer(properties);
                    String finalMessage = dataProducer.generateRate(currencyRate);
                    output.println(finalMessage);
                }else{
                    output.println("(-)|invalid-currency-pair");
                }
            }
        }catch (IOException error){
            error.printStackTrace();
        }finally{
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
