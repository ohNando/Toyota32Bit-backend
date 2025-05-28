package main.Rate;

import main.Producer.RateProducer;
import main.Server.Server;

import java.time.Instant;

public class Rate extends Thread {
    private String rateName;
    private float bid;
    private float ask;
    private Instant timestamp;

    private Server server;

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
