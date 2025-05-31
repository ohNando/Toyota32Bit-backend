package com.toyota.toyotabackend.restapi.entity;

import com.toyota.toyotabackend.restapi.producer.RateProducer;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

/**
 * A Data Transfer Object (DTO) representing a currency rate.
 * This class is used to transfer rate information such as the rate name,
 * bid, ask, and the time the rate was last updated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
        setRateName(_rateName);
        setBid(_bid);
        setAsk(_ask);
    }

    public void stopRate(){
        isActive = false;
    }

    @Override
    public void run(){
        do{
            float[] rateParts = RateProducer.generateRates(this.rateName);
            setBid(rateParts[0]);
            setAsk(rateParts[1]);

            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }
            setRateUpdateTime(Instant.now());
        }while(isActive);
    }
}
