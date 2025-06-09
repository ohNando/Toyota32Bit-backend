package Rate;

import Producer.RateProducer;
import Server.Server;

import java.time.Instant;

/**
 * Represents a currency rate with bid and ask prices.
 * <p>
 * Extends Thread to continuously update its bid and ask values
 * periodically while the server is running.
 * </p>
 */
public class Rate extends Thread {
    private String rateName;
    private float bid;
    private float ask;
    private Instant timestamp;

    private Server server;

    /**
     * Constructs a Rate object.
     * Initializes the rate with a name, bid, ask, and sets the initial timestamp.
     * Also associates it with the server to check server running status.
     *
     * @param _Server the server instance controlling the lifecycle
     * @param _rateName the name of the rate (e.g., currency pair)
     * @param _bid initial bid price
     * @param _ask initial ask price
     */
    public Rate(Server _Server,String _rateName, float _bid, float _ask) {
        this.setRateName(_rateName);
        this.setBid(_bid);
        this.setAsk(_ask);
        this.setTimestamp(Instant.now());

        this.server = _Server;
    }

    public String getRateName() { return rateName; }
    public float getBid() { return bid; }
    public float getAsk() { return ask; }
    public Instant getTimestamp() { return timestamp; }

    public void setRateName(String rateName) { this.rateName = rateName; }
    public void setBid(float bid) { this.bid = bid; }
    public void setAsk(float ask) { this.ask = ask; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    /**
     * Thread execution method.
     * <p>
     * Continuously generates new bid and ask rates every second
     * while the associated server is running.
     * Updates timestamp after each rate generation.
     * </p>
     */
    @Override
    public void run() {
        do{
            float[] rateParts = RateProducer.generateRate(rateName);
            setBid(rateParts[0]);
            setAsk(rateParts[1]);

            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            setTimestamp(Instant.now());
        }while(server.getServerStatus());
    }
}
