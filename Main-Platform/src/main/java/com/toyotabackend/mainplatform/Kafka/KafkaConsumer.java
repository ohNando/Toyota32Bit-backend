package com.toyotabackend.mainplatform.Kafka;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Repository.PostgresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @Autowired
    private PostgresService postgresService;

    @KafkaListener(topics = "${kafka.topic.name}",groupId = "my-group")
    public void listen(String message) {
        RateDto dto = parseRateData(message);
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
