package com.toyotabackend.mainplatform.Dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a rate with bid and ask prices,
 * as well as the time of the last update.
 * <p>
 * This class is used for transferring rate data between components
 * such as REST controllers, Kafka producers/consumers, and caching layers.
 */
@Getter
@Setter
public class RateDto implements Serializable {

    /**
     * The name of the rate (e.g., currency pair like USDTRY).
     */
    private String rateName;

    /**
     * The bid price for the rate.
     */
    private float bid;

    /**
     * The ask price for the rate.
     */
    private float ask;

    /**
     * The timestamp when the rate was last updated, in ISO-8601 format.
     */
    private Instant timestamp;

    private RateStatus status;
    public RateDto() {};

    public RateDto(String _rateName,float _bid,float _ask,Instant _timestamp){
        this.setRateName(_rateName);
        this.setAsk(_ask);
        this.setBid(_bid);
        this.setTimestamp(_timestamp);
        this.setStatus(RateStatus.NOT_AVAILABLE);
    }
}
