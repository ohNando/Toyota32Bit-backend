package com.toyotabackend.mainplatform.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toyotabackend.mainplatform.Dto.RateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service for sending rate data to a Kafka topic.
 * <p>
 * This service is responsible for converting rate data into a formatted message
 * and sending it to a Kafka topic for further processing.
 */
@Service
public class KafkaProducer {

    @Value("${kafka.topic.name}")
    private String topic;  // The Kafka topic name to send messages to

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Constructs a new KafkaProducer with the provided KafkaTemplate.
     *
     * @param kafkaTemplate the KafkaTemplate used for sending messages to Kafka
     */
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a formatted message containing rate data to the Kafka topic.
     * <p>
     * The message is formatted as follows:
     * {@code platformName_rateName|bid|ask|rateUpdateTime}
     *
     * @param platformName the platform name (e.g., "PF1", "PF2")
     * @param rateName the name of the rate (e.g., "USDTRY")
     * @param rateDto the rate data to be sent, encapsulated in a {@link RateDto}
     * @throws JsonProcessingException if there is an error during message creation or sending
     */
    public void send(String platformName, String rateName, RateDto rateDto) throws JsonProcessingException {
        // Format the message to include platform name, rate name, bid, ask, and rate update time
        String message = String.format("%s_%s|%.2f|%.2f|%s",
                platformName,
                rateName,
                rateDto.getBid(),
                rateDto.getAsk(),
                rateDto.getRateUpdateTime());

        // Send the message to the Kafka topic
        kafkaTemplate.send(topic, message);
    }
}
