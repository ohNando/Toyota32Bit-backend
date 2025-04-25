package Server.Producer;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;

/**
 * A thread that generates and sends currency rate data for a specific currency pair.
 * It produces a bid and ask rate at regular intervals based on a base rate defined
 * in the properties and sends the data to the provided output stream.
 */
public class RateProducerThread extends Thread {
    private final String currencyRate;
    private final PrintWriter output;
    private final Properties properties;
    private final Random random;
    private volatile boolean isRunning = true;

    /**
     * Constructs a RateProducerThread for a given currency rate.
     *
     * @param currencyRate The currency rate to generate data for.
     * @param properties The configuration properties that include the base rate.
     * @param output The output stream to send the generated rates to.
     */
    public RateProducerThread(String currencyRate, Properties properties, PrintWriter output) {
        this.currencyRate = currencyRate;
        this.properties = properties;
        this.output = output;
        this.random = new Random();
    }

    /**
     * Retrieves the base rate for the given currency pair from the properties.
     *
     * @return The base rate for the currency pair.
     * @throws IllegalArgumentException If the base rate for the currency pair is not found.
     */
    private double getBaseRate() {
        String rateString = properties.getProperty("rates.base." + currencyRate);
        if (rateString != null) {
            return Double.parseDouble(rateString);
        } else {
            throw new IllegalArgumentException("(-)|Invalid currency pair");
        }
    }

    /**
     * Generates a bid and ask rate based on the base rate and random spread values.
     * The generated rates are formatted with the currency pair and a timestamp.
     *
     * @return A formatted string containing the bid, ask, and timestamp.
     */
    private String generateRate() {
        double baseRate = getBaseRate();
        double spread = 0.08;
        double bid = baseRate + (spread * random.nextDouble());
        double ask = bid + (0.1 + spread * random.nextDouble());
        String timestamp = Instant.now().toString();
        return String.format("PF1_%s|22:number:%.15f|25:number:%.15f|5:timestamp:%s",
                currencyRate, bid, ask, timestamp);
    }

    /**
     * Runs the thread, generating and sending currency rates at 1-second intervals
     * until the thread is stopped.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                String finalMessage = generateRate();
                output.println(finalMessage);
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Stops the thread from generating and sending rates.
     * This method is used to cleanly shut down the thread.
     */
    public void stopThread() {
        isRunning = false;
        this.interrupt();
    }
}
