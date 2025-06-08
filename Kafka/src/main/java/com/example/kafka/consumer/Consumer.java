package com.example.kafka.consumer;

import com.example.kafka.database.PostgreSQLService;
import com.example.kafka.entity.Rate;
import com.example.kafka.mapper.RateMapper;
import com.example.kafka.opensearch.OpensearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Consumer {
    @Autowired
    PostgreSQLService postgreSQLService;
    @Autowired
    OpensearchService OSService;

    @KafkaListener(topics = "rates", groupId = "my-group")
    public void consumeRateEvents(@Payload String message) {
        if(message == null || message.isEmpty()) return;
        Rate rateToSave = null;
        try{
            rateToSave = RateMapper.stringToRate(message);
        }catch (Exception e){
            throw new RuntimeException("Failed to parse rate message: " + message,e);
        }

        if(rateToSave.getRateName() == null) return;
        try {
            postgreSQLService.updateRates(rateToSave);
            OSService.updateRate(rateToSave);
        }catch (Exception e){
            throw new RuntimeException("Failed to update rate " + rateToSave.getRateName(),e);
        }
    }
}
