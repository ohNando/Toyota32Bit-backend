package com.toyotabackend.mainplatform.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toyotabackend.mainplatform.Dto.RateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    @Value("${kafka.topic.name}")
    private String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void send(String platformName,String rateName,RateDto rateDto) throws JsonProcessingException {
        String message = String.format("%s_%s|%.2f|%.2f|%s",
                platformName,
                rateName,
                rateDto.getBid(),
                rateDto.getAsk(),
                rateDto.getRateUpdateTime());
        kafkaTemplate.send(topic, message);
    }
}
