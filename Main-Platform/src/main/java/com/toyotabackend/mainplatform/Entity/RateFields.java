package com.toyotabackend.mainplatform.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the basic fields of a rate including bid, ask, and timestamp.
 * <p>
 * This class is typically used for transferring updated rate values
 * without the full Rate entity context.
 */
@Getter
@Setter
@AllArgsConstructor
public class RateFields {

    /**
     * The bid price of the rate.
     */
    private float bid;

    /**
     * The ask price of the rate.
     */
    private float ask;

    /**
     * The timestamp indicating when the rate was last updated.
     */
    private String timestamp;
}
