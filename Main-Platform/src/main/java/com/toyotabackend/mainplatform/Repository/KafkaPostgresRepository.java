package com.toyotabackend.mainplatform.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;
import com.toyotabackend.mainplatform.Kafka.KafkaProducer;
import com.toyotabackend.mainplatform.Mapper.RateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for handling the saving of rate data to both Kafka and PostgreSQL.
 * <p>
 * This class provides functionality to send rate data to Kafka for messaging and also
 * save the data to a PostgreSQL database. It utilizes the {@link KafkaProducer} for
 * sending messages to Kafka and {@link PostgresService} for saving the rate data into
 * the database.
 */
@Repository
public class KafkaPostgresRepository {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private PostgresService postgresService;

    /**
     * Saves a list of rate data to Kafka and PostgreSQL.
     * <p>
     * This method iterates over a list of {@link RateDto} objects, sends each one to Kafka,
     * and then saves the corresponding {@link Rate} entity to the PostgreSQL database.
     *
     * @param platformName the name of the platform from which the rate data is coming
     * @param rateName     the name of the rate
     * @param rateDtoList  a list of {@link RateDto} objects containing the rate data
     * @throws JsonProcessingException if an error occurs while serializing the rate data to JSON
     */
    public void saveRates(String platformName, String rateName, List<RateDto> rateDtoList) throws JsonProcessingException {
        for (RateDto rateDto : rateDtoList) {
            kafkaProducer.send(platformName, rateName, rateDto);  // Sending rate data to Kafka
            Rate rate = RateMapper.mapToRate(rateDto);  // Mapping RateDto to Rate entity
            postgresService.saveRate(rate);  // Saving rate entity to PostgreSQL
        }
    }
}
