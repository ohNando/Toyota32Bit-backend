package Server.Producer;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that manages subscription and unsubscription of currency rates.
 * It spawns and manages threads for each subscribed currency rate that produce
 * rate data based on the configuration.
 */
public class RateDataProducer {
    private final Properties properties;
    private final Map<String, RateProducerThread> rateProducerThreads = new ConcurrentHashMap<>();

    /**
     * Constructs a RateDataProducer with the given properties.
     *
     * @param properties The configuration properties for the rate producer threads.
     */
    public RateDataProducer(Properties properties) {
        this.properties = properties;
    }

    /**
     * Subscribes to a currency rate by starting a new rate producer thread for the specified rate.
     * If already subscribed, sends a message indicating so.
     *
     * @param currencyRate The currency rate to subscribe to.
     * @param output The output stream to send messages to the client.
     */
    public synchronized void subscribe(String currencyRate, PrintWriter output) {
        if (rateProducerThreads.containsKey(currencyRate)) {
            System.out.println("(?)|Already subscribed to " + currencyRate);
        } else {
            RateProducerThread rateProducerThread = new RateProducerThread(currencyRate, properties, output);
            rateProducerThreads.put(currencyRate, rateProducerThread);
            new Thread(rateProducerThread).start();
            System.out.println("(+)|Subscribed to " + currencyRate);
        }
    }

    /**
     * Unsubscribes from a currency rate by stopping the associated rate producer thread.
     * If not currently subscribed, sends a message indicating the invalid request.
     *
     * @param currencyRate The currency rate to unsubscribe from.
     * @param output The output stream to send messages to the client.
     */
    public synchronized void unsubscribe(String currencyRate, PrintWriter output) {
        RateProducerThread rateProducerThread = rateProducerThreads.get(currencyRate);
        if (rateProducerThread != null) {
            rateProducerThread.stopThread();
            try {
                rateProducerThread.join();
                rateProducerThreads.remove(currencyRate);
                System.out.println("(+)|Unsubscribed from " + currencyRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.out.println("(?)|Invalid unsubscribe request: " + currencyRate);
        }
    }
}
