package com.example.kafka.consumer;

import com.example.kafka.database.PostgreSQLService;
import com.example.kafka.entity.Rate;
import com.example.kafka.mapper.RateMapper;
import com.example.kafka.opensearch.OpensearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer component that listens to the "rates" topic
 * and processes incoming rate data by updating both PostgreSQL
 * and OpenSearch storage systems.
 */
@Component
public class Consumer {
    /** Service responsible for PostgreSQL database operations */
    @Autowired
    PostgreSQLService postgreSQLService;
    /** Service responsible for OpenSearch index updates */
    @Autowired
    OpensearchService OSService;

    /**
     * Kafka listener method for the "rates" topic.
     * Converts the incoming message into a {@link Rate} object and
     * updates the corresponding records in PostgreSQL and OpenSearch.
     *
     * @param message the JSON-formatted rate data received from Kafka
     * @throws RuntimeException if the message cannot be parsed or if update operations fail
     */
    @KafkaListener(topics = "rates", groupId = "my-group")
    public void consumeRateEvents(@Payload String message) {
        if (message == null || message.isEmpty()) return;
        Rate rateToSave = RateMapper.stringToRate(message);

        if (rateToSave == null || rateToSave.getRateName() == null) {
            throw new RuntimeException("Failed to parse rate message: " + message);
        }

        try {
            postgreSQLService.updateRate(rateToSave);
            OSService.updateRate(rateToSave);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update rate " + rateToSave.getRateName(), e);
        }
    }
}
