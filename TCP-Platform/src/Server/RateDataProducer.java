package Server;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;

public class RateDataProducer {
    private Properties properties;
    private Random random;

    public RateDataProducer(Properties properties){
        this.properties = properties;
        this.random = new Random();
    }

    public String generateRate(String currencyRate){
        double baseRate = getBaseRate(currencyRate);
        if(baseRate == 0){
            return null;
        }
        double spread = 0.08;
        double bid = baseRate + (spread * random.nextDouble());
        double ask = bid + (0.1 + spread * random.nextDouble());
        String timestamp = Instant.now().toString();
        return String.format("%s|22:number:%.15f|25:number:%.15f|5:timestamp:%s"
                , currencyRate, bid, ask, timestamp);

    }

    private double getBaseRate(String currencyRate){
        String key = "rates.base." + currencyRate;
        String rateString = properties.getProperty(key);

        if(rateString != null){
            return Double.parseDouble(rateString);
        }else{
            return 0;
        }
    }
}
