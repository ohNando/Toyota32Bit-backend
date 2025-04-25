package Server.Producer;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class RateDataProducer {
    private final Properties properties;
    private final Map<String, RateProducerThread> rateProducerThreads = new ConcurrentHashMap<>();


    public RateDataProducer(Properties properties) {
        this.properties = properties;
    }

    public synchronized void subscribe(String currencyRate, PrintWriter output) {
        if (rateProducerThreads.containsKey(currencyRate)) {
            output.println("(?)|Already subscribed to " + currencyRate);
        }else{
            RateProducerThread rateProducerThread = new RateProducerThread(currencyRate,properties,output);
            rateProducerThreads.put(currencyRate, rateProducerThread);
            new Thread(rateProducerThread).start();
            output.println("(+)|Subscribed to " + currencyRate);
        }
    }

    public synchronized void unsubscribe(String currencyRate, PrintWriter output) {
        RateProducerThread rateProducerThread = rateProducerThreads.get(currencyRate);
        if (rateProducerThread != null) {
            rateProducerThread.stopThread();
            try {
                rateProducerThread.join();
                rateProducerThreads.remove(currencyRate);
                output.println("(+)|Unsubscribed from " + currencyRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            output.println("(?)|Invalid unsubscribe request: " + currencyRate);
        }
    }
}
