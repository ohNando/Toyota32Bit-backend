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

@Component
public class DatabaseService {
    @Autowired
    private RateRepository repository;
    private final Logger logger = LoggerFactory.getLogger("DatabaseLogger");
    DatabaseService(){};


    public void updateRates(List<RateDto> dtoList){
        logger.info("Rate saving is beginning...");
        List<Rate> rateList = new ArrayList<>();
        for(RateDto dto : dtoList){
            rateList.add(RateMapper.mapToRate(dto));
        }
        repository.saveAll(rateList);
        logger.info("Rates are saved for database");
    }

    public RateDto getLastestRate(String rateName){
        logger.info("Last rate is fetching : ",rateName);
        Optional<Rate> rate = repository.findTopByOrderByDbUpdateTimeDesc();
        if(rate.isEmpty()){
            logger.warn("Rate is not found : ",rateName);
            return null;
        }
        logger.info("Rate {} fetched from database",rateName);
        return RateMapper.mapToRateDto(rate.get());
    }
}
