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
        ){
            String acceptedMessage = input.readLine();

            if(acceptedMessage.startsWith("subscribe|PF1_")){
                String currencyRate = acceptedMessage.split("\\|")[1];
                if(isValidRate(currencyRate)){
                    RateDataProducer dataProducer = new RateDataProducer(properties);
                    String rateData = dataProducer.generateRate(currencyRate);
                    output.println(rateData);
                }else{
                    output.println("(-)(error)|invalid-currency-pair");
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

    private boolean isValidRate(String currencyPair){
        String allowedRates = properties.getProperty("rates.allowed-rates");
        return allowedRates != null && allowedRates.contains(currencyPair);
    }
}
