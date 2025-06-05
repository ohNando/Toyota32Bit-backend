package com.example.kafka.database;

import com.example.kafka.entity.Rate;
import com.example.kafka.repository.RateRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        private final Logger logger = LoggerFactory.getLogger("DatabaseLogger");

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
                logger.info("Rate list is empty or null. No action will be performed.");
                return;
            }
            logger.info("Rate saving is beginning...");
            List<Rate> rateToSave = new ArrayList<>();
            for (Rate rate : rateList) {
                if(rate == null || rate.getRateName() == null) {
                    logger.warn("Skipping null rate!");
                    continue;
                }
                Optional<Rate> existRate = repository.findByRateName(rate.getRateName());
                Rate rateToPersist;
                if(existRate.isPresent()) {
                    rateToPersist = existRate.get();
                    logger.info("Exist rate is found {}. Updating rate...", rate.getRateName());
                    rateToPersist.setBid(rate.getBid());
                    rateToPersist.setAsk(rate.getAsk());
                    rateToPersist.setRateUpdateTime(rate.getRateUpdateTime());
                }else{
                    logger.info("No existing rate for {}. Creating new rate...", rate.getRateName());
                    rateToPersist = rate;
                }
                rateToSave.add(rateToPersist);
            }
            if(!rateToSave.isEmpty()) {
                repository.saveAll(rateToSave);
                logger.info("{} rates saved/updated in database.",rateToSave.size());
            }else{
                logger.info("No valid rate to save/update.");
            }
        }
}
