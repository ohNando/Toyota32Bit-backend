package com.toyota.toyotabackend.restapi.dto;

import lombok.*;

/**
 * A Data Transfer Object (DTO) representing a currency rate.
 * This class is used to transfer rate information such as the rate name,
 * bid, ask, and the time the rate was last updated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {

    /**
     * The name of the currency pair (e.g., "USD/TRY").
     */
    private String rateName;

    /**
     * The bid price of the currency pair.
     */
    private double bid;

    /**
     * The ask price of the currency pair.
     */
    private double ask;

/**
 * The timestamp when the rate was last updated
 */
    private String rateUpdateTime;
}
