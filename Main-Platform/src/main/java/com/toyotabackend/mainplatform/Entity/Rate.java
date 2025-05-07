package com.toyotabackend.mainplatform.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing a rate record stored in the database.
 * <p>
 * Maps to the "rate" table and holds bid/ask prices and the update timestamp.
 */
@Entity
@Table(name = "rate")
public class Rate {

    /**
     * Unique identifier for each rate entry (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    /**
     * Unique name of the rate (e.g., USDTRY). Cannot be null.
     */
    @Column(unique = true, nullable = false, length = 10)
    public String rateName;

    /**
     * The bid price for the rate. Cannot be null.
     */
    @Column(nullable = false)
    public float bid;

    /**
     * The ask price for the rate. Cannot be null.
     */
    @Column(nullable = false)
    public float ask;

    /**
     * Timestamp indicating the last time this rate was updated. Cannot be null.
     */
    @Column(nullable = false)
    public Instant rateUpdateTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp dbUpdateTime;

    /**
     * Constructor for creating a Rate object with all required fields.
     *
     * @param _rateName       the name of the rate
     * @param _bid            the bid price
     * @param _ask            the ask price
     * @param _rateUpdateTime the time the rate was last updated
     */
    public Rate(String _rateName, float _bid, float _ask, Instant _rateUpdateTime,Timestamp _dbUpdateTme) {
        this.rateName = _rateName;
        this.bid = _bid;
        this.ask = _ask;
        this.rateUpdateTime = _rateUpdateTime;
        this.dbUpdateTime = _dbUpdateTme;
    }

    // Default constructor (required by JPA)
    public Rate() {}
}
