package com.toyotabackend.mainplatform.Entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing a rate record stored in the database.
 * <p>
 * Maps to the "rate" table and holds bid/ask prices and the update timestamp.
 */
@Getter
@Setter
@Entity
@Table(name = "rate")
public class Rate {

    /**
     * Unique identifier for each rate entry (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Unique name of the rate (e.g., USDTRY). Cannot be null.
     */
    @Column(unique = true, nullable = false, length = 10)
    private String rateName;

    /**
     * The bid price for the rate. Cannot be null.
     */
    @Column(nullable = false)
    private float bid;

    /**
     * The ask price for the rate. Cannot be null.
     */
    @Column(nullable = false)
    private float ask;

    /**
     * Timestamp indicating the last time this rate was updated. Cannot be null.
     */
    @Column(nullable = false)
    private String rateUpdateTime;

    /**
     * Constructor for creating a Rate object with all required fields.
     *
     * @param _rateName       the name of the rate
     * @param _bid            the bid price
     * @param _ask            the ask price
     * @param _rateUpdateTime the time the rate was last updated
     */
    public Rate(String _rateName, float _bid, float _ask, String _rateUpdateTime) {
        this.rateName = _rateName;
        this.bid = _bid;
        this.ask = _ask;
        this.rateUpdateTime = _rateUpdateTime;
    }

    // Default constructor (required by JPA)
    public Rate() {}
}
