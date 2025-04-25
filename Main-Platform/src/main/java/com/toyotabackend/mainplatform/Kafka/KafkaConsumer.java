package com.toyotabackend.mainplatform.Kafka;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;
import com.toyotabackend.mainplatform.Mapper.RateMapper;
import com.toyotabackend.mainplatform.Repository.PostgresService;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "${kafka.topic.name}",groupId = "my-group")
    public void listen(String message) {
        RateDto dto = parseRateData(message);
        if(dto!=null){
            Rate rate = RateMapper.mapToRate(dto);
            PostgresService postgresService = new PostgresService();
            postgresService.saveRate(rate);
        }
    }

    private RateDto parseRateData(String message) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(message, RateDto.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
