package Server;

import java.io.PrintWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RateDataProducer {
    private final Properties properties;
    private final Random random;
    private Map<String, Thread> rateProducerThreads = new HashMap<>();
    private Map<String, AtomicBoolean> keep = new HashMap<>();

    public RateDataProducer(Properties properties) {
        this.properties = properties;
        this.random = new Random();
    }

    private double getBaseRate(String currencyRate) {
        String rateString = properties.getProperty("rates.base." + currencyRate);

        if (rateString != null) {
            return Double.parseDouble(rateString);
        } else {
            throw new IllegalArgumentException("(-)|Invalid currency pair");
        }
    }

    private String generateRate(String currencyRate) {
        double baseRate = getBaseRate(currencyRate);
        if (baseRate == 0) {
            return null;
        }
        double spread = 0.08;
        double bid = baseRate + (spread * random.nextDouble());
        double ask = bid + (0.1 + spread * random.nextDouble());
        String timestamp = Instant.now().toString();
        return String.format("PF1_%s|22:number:%.15f|25:number:%.15f|5:timestamp:%s",
                currencyRate, bid, ask, timestamp);
    }

    public void subscribe(String currencyRate, PrintWriter output) {
        try{
            AtomicBoolean isRunning = new AtomicBoolean(true);
            keep.put(currencyRate, isRunning);
            Thread rateProducerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String finalMessage = generateRate(currencyRate);
                        output.println(finalMessage);
                        Thread.sleep(1000);
                    } catch (InterruptedException error) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                System.out.println(currencyRate + " için thread sonlandırıldı.");
            });
            rateProducerThread.start();
            rateProducerThreads.put(currencyRate, rateProducerThread);
        }catch (Exception error){
            error.printStackTrace();
        }
    }

    public void unsubscribe(String currencyRate) {
        System.out.println("(!) = "+ currencyRate);
        AtomicBoolean isRunning = keep.get(currencyRate);

        if (isRunning != null) {
            isRunning.set(false);
            Thread rateProducerThread = rateProducerThreads.get(currencyRate);

            if (rateProducerThread != null && rateProducerThread.isAlive()) {
                rateProducerThread.interrupt();
                try {
                    rateProducerThread.join();
                } catch (InterruptedException e) {
                    System.err.println("Thread kesintiye uğradı: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
                rateProducerThreads.remove(currencyRate);
                keep.remove(currencyRate);
                System.out.println(currencyRate + " için abonelik iptal edildi ve thread durduruldu.");
            } else {
                System.out.println(currencyRate + " için thread bulunamadı veya zaten durdurulmuş.");
            }
        } else {
            System.out.println(currencyRate + " için abonelik bulunamadı.");
        }
    }
}