package com.toyotabackend.mainplatform.RateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;
import com.toyotabackend.mainplatform.Mapper.RateMapper;
import com.toyotabackend.mainplatform.Repository.RateRepository;

/**
 * Service class responsible for interacting with the database to fetch and save rate data.
 * The class provides methods to update rates in the database and fetch the latest rate for a given name.
 */
@Component("postgreSQL")
public class DatabaseService {
    
    @Autowired
    private RateRepository repository;
    private final Logger logger = LoggerFactory.getLogger("DatabaseLogger");
    
    /**
     * Default constructor for DatabaseService.
     */
    DatabaseService() {}

    /**
     * Saves a list of RateDto objects into the database.
     * This method maps the RateDto to the corresponding Rate entity and saves it using the repository.
     * 
     * @param dtoList List of RateDto objects to be saved.
     */
    public void updateRates(List<RateDto> dtoList) {
        logger.info("Rate saving is beginning...");
        List<Rate> rateList = new ArrayList<>();
        for (RateDto dto : dtoList) {
            rateList.add(RateMapper.mapToRate(dto));
        }
        repository.saveAll(rateList);
        logger.info("Rates are saved for database");
    }

    /**
     * Fetches the latest rate from the database.
     * This method retrieves the most recent rate from the database, sorted by the update timestamp.
     * 
     * @param rateName The name of the rate to be fetched.
     * @return The latest rate as a RateDto, or null if no rate is found.
     */
    public RateDto getLastestRate(String rateName) {
        logger.info("Last rate is fetching : {}", rateName);
        Optional<Rate> rate = repository.findTopByOrderByDbUpdateTimeDesc();
        if (rate.isEmpty()) {
            logger.warn("Rate is not found : {}", rateName);
            return null;
        }
        logger.info("Rate {} fetched from database", rateName);
        return RateMapper.mapToRateDto(rate.get());
    }
}
