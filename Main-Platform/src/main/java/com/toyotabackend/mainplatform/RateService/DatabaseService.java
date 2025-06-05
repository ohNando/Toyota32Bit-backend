package com.toyotabackend.mainplatform.RateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
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
    @Transactional // For this entire method run as a single database operation
    public void updateRates(List<RateDto> dtoList) {
        if(dtoList == null || dtoList.isEmpty()) {
            logger.info("Rate list is empty or null. No action will be performed.");
            return;
        }
        logger.info("Rate saving is beginning...");
        List<Rate> rateToSave = new ArrayList<>();
        for (RateDto dto : dtoList) {
            if(dto == null || dto.getRateName() == null) {
                logger.warn("Skipping null rate!");
                continue;
            }
            Optional<Rate> existRate = repository.findByRateName(dto.getRateName());
            Rate rateToPersist;
            if(existRate.isPresent()) {
                rateToPersist = existRate.get();
                logger.info("Exist rate is found {}. Updating rate...", dto.getRateName());
                rateToPersist.setBid(dto.getBid());
                rateToPersist.setAsk(dto.getAsk());
                rateToPersist.setRateUpdateTime(dto.getRateUpdateTime());
            }else{
                logger.info("No existing rate for {}. Creating new rate...", dto.getRateName());
                rateToPersist = RateMapper.mapToRate(dto);
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
