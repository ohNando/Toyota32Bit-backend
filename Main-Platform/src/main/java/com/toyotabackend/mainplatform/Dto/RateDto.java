package com.toyotabackend.mainplatform.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a rate with bid and ask prices,
 * as well as the time of the last update.
 * <p>
 * This class is used for transferring rate data between components
 * such as REST controllers, Kafka producers/consumers, and caching layers.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RateDto {

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
    private String rateUpdateTime;
}
