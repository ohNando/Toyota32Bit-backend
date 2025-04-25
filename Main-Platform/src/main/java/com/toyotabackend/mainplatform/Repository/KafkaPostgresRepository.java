package com.toyotabackend.mainplatform.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;
import com.toyotabackend.mainplatform.Kafka.KafkaProducer;
import com.toyotabackend.mainplatform.Mapper.RateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KafkaPostgresRepository {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private PostgresService postgresService;

    public void saveRates(String platformName,String rateName,List<RateDto> rateDtoList) throws JsonProcessingException {
        for(RateDto rateDto : rateDtoList){
            kafkaProducer.send(platformName,rateName,rateDto);
            Rate rate = RateMapper.mapToRate(rateDto);
            postgresService.saveRate(rate);
        }
    }
}
