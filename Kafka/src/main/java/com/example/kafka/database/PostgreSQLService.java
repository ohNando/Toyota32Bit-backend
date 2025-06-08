package com.example.kafka.database;

import com.example.kafka.entity.Rate;
import com.example.kafka.repository.RateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
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
         * @param rateList List of RateDto objects to be saved.
         */
        @Transactional // For this entire method run as a single database operation
        public void updateRates(List<Rate> rateList) {
            if(rateList == null || rateList.isEmpty()) {
                System.out.println("Rate list is empty or null. No action will be performed.");
                return;
            }
            System.out.println("Rate saving is beginning...");
            List<Rate> rateToSave = new ArrayList<>();
            for (Rate rate : rateList) {
                if(rate == null || rate.getRateName() == null) {
                    System.err.println("Skipping null rate!");
                    continue;
                }
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
                rateToSave.add(rateToPersist);
            }
            if(!rateToSave.isEmpty()) {
                repository.saveAll(rateToSave);
                System.out.println(rateToSave.size() + " rates saved/updated in database.");
            }else{
                System.err.println("No valid rate to save/update.");
            }
        }
}
