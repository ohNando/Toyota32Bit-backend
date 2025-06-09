package com.example.kafka.database;

import com.example.kafka.entity.Rate;
import com.example.kafka.repository.RateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Optional;


@Component
public class PostgreSQLService {
    /**
     * Service class responsible for interacting with the database to fetch and save rate data.
     * The class provides methods to update rates in the database and fetch the latest rate for a given name.
     */
        @Autowired
        private RateRepository repository;

        /**
         * Default constructor for DatabaseService.
         */
        PostgreSQLService() {}

        /**
         * Saves a list of RateDto objects into the database.
         * This method maps the RateDto to the corresponding Rate entity and saves it using the repository.
         *
         * @param rate List of RateDto objects to be saved.
         */
        @Transactional // For this entire method run as a single database operation
        public void updateRate(Rate rate) {
            if(rate == null || rate.getRateName() == null) {
                System.out.println("Rate list is empty or null. No action will be performed.");
                return;
            }
            System.out.println("Rate saving/updating is beginning...");

            Optional<Rate> existRate = repository.findByRateName(rate.getRateName());
            Rate rateToPersist;

            if(existRate.isPresent()) {
                rateToPersist = existRate.get();
                System.out.println("Exist rate is found " + rate.getRateName() + ". Updating rate...");
                rateToPersist.setBid(rate.getBid());
                rateToPersist.setAsk(rate.getAsk());
                rateToPersist.setRateUpdateTime(rate.getRateUpdateTime());
            }else{
                System.err.println("No existing rate for "+ rate.getRateName() +". Creating new rate...");
                rateToPersist = rate;
            }

            try{
                repository.save(rateToPersist);
                System.out.println("Rate saved in PostgreSQL..." + rateToPersist.getRateName());
            }catch(Exception e){
                System.err.println("Error saving rate " + rateToPersist.getRateName() + " " + e.getMessage());
                throw e;
            }

        }
}
