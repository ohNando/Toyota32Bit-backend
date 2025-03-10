package Server;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;

public class RateProducerThread extends Thread{
    private final String currencyRate;
    private final PrintWriter output;
    private final Properties properties;
    private final Random random;
    private volatile boolean isRunning = true;

    public RateProducerThread(String currencyRate, Properties properties, PrintWriter output) {
        this.currencyRate = currencyRate;
        this.properties = properties;
        this.output = output;
        this.random = new Random();
    }

    private double getBaseRate() {
        String rateString = properties.getProperty("rates.base." + currencyRate);
        if (rateString != null) {
            return Double.parseDouble(rateString);
        } else {
            throw new IllegalArgumentException("(-)|Invalid currency pair");
        }
    }

    private String generateRate() {
        double baseRate = getBaseRate();
        double spread = 0.08;
        double bid = baseRate + (spread * random.nextDouble());
        double ask = bid + (0.1 + spread * random.nextDouble());
        String timestamp = Instant.now().toString();
        return String.format("PF1_%s|22:number:%.15f|25:number:%.15f|5:timestamp:%s",
                currencyRate, bid, ask, timestamp);
    }

    @Override
    public void run(){
        try{
            while(isRunning){
                String finalMessage = generateRate();
                output.println(finalMessage);
                Thread.sleep(1000);
            }
        }catch(InterruptedException ignored){
        }
    }

    public void stopThread(){
        isRunning = false;
        this.interrupt();
    }
}
