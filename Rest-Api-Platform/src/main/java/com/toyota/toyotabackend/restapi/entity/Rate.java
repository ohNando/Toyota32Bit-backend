package com.toyota.toyotabackend.restapi.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Random;

/**
 * A Data Transfer Object (DTO) representing a currency rate.
 * This class is used to transfer rate information such as the rate name,
 * bid, ask, and the time the rate was last updated.
 */
@Getter
@Setter
public class Rate extends Thread{
    /**
     * The name of the currency pair.
     */
    private String rateName;

    /**
     * The bid price of the currency pair.
     */
    private float bid;

    /**
     * The ask price of the currency pair.
     */
    private float ask;

    /**
     * The timestamp when the rate was last updated
     */
    private Instant rateUpdateTime;

    private boolean isActive = true;

    public Rate(String _rateName, float _bid, float _ask) {
        this.setRateName(_rateName);
        this.setBid(_bid);
        this.setAsk(_ask);
    }

    public void stopRate(){
        isActive = false;
    }

    @Override
    public void run(){
        Random random = new Random();
        boolean incOrDec;
        float randRatePerc;

        do{
            float minPerc = 0.001f;
            float maxPerc = 0.005f;

            randRatePerc = minPerc + random.nextFloat() * (maxPerc - minPerc);

            incOrDec = random.nextBoolean();
            if (incOrDec) {
                setBid(bid + (bid * randRatePerc));
                setAsk(ask + (ask * randRatePerc));
            }else{
                setBid(bid - (bid * randRatePerc));
                setAsk(ask - (ask * randRatePerc));
            }

            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }

            setRateUpdateTime(Instant.now());
        }while(isActive);
    }
}
