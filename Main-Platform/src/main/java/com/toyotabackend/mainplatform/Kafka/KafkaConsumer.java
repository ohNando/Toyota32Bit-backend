package com.toyotabackend.mainplatform.Kafka;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;
import com.toyotabackend.mainplatform.Mapper.RateMapper;
import com.toyotabackend.mainplatform.Repository.PostgresService;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service for processing incoming messages from Kafka topics.
 * <p>
 * This service listens to a specific Kafka topic, parses the incoming messages,
 * and stores the parsed data into a Postgres database.
 */
@Service
public class KafkaConsumer {

    /**
     * Listens to the Kafka topic and processes the messages.
     *
     * @param message the incoming Kafka message in String format
     */
    @KafkaListener(topics = "${kafka.topic.name}", groupId = "my-group")
    public void listen(String message) {
        RateDto dto = parseRateData(message);
        if (dto != null) {
            Rate rate = RateMapper.mapToRate(dto);
            PostgresService postgresService = new PostgresService();
            postgresService.saveRate(rate);  // Save the parsed rate into the database
        }
    }

    /**
     * Parses the incoming message and converts it into a {@link RateDto} object.
     *
     * @param message the JSON string representing the rate data
     * @return the parsed {@link RateDto} object, or null if parsing fails
     */
    private RateDto parseRateData(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(message, RateDto.class);  // Convert JSON to RateDto
        } catch (Exception e) {
            e.printStackTrace();  // Print the stack trace if there is an error during parsing
            return null;
        }
    }
}
