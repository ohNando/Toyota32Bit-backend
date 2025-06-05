package com.example.kafka.consumer;

import com.example.kafka.database.PostgreSQLService;
import com.example.kafka.entity.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Consumer {
    @Autowired
    PostgreSQLService postgreSQLService;

    private final EventConsumer consumer;

    public Consumer(){
        this.consumer = new EventConsumer();
    }

    @KafkaListener(topics = "rates", groupId = "my-group")
    public void consumeRateEvents(String message) {
        List<Rate> rateList = consumer.consumeRate();
        postgreSQLService.updateRates(rateList);
    }
}
