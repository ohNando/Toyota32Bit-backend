package com.toyotabackend.mainplatform.Repository;

import com.toyotabackend.mainplatform.Entity.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for saving {@link Rate} entities to the PostgreSQL database.
 * <p>
 * This class acts as a wrapper around the {@link RateRepository} to facilitate the
 * saving of rate data into the database. It provides a method to save a single {@link Rate}
 * entity.
 */
@Service
public class PostgresService {

    @Autowired
    private RateRepository rateRepository;

    /**
     * Saves a {@link Rate} entity to the PostgreSQL database.
     * <p>
     * This method uses the {@link RateRepository} to persist the provided {@link Rate}
     * entity to the database.
     *
     * @param rate the {@link Rate} entity to be saved
     */
    public void saveRate(Rate rate){
        rateRepository.save(rate);  // Saving the Rate entity to the database
    }
}
