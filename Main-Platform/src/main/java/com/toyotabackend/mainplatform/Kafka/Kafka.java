package com.toyotabackend.mainplatform.Kafka;

import java.util.List;

import com.toyotabackend.mainplatform.Dto.RateDto;

/**
 * The Kafka class serves as a wrapper around Kafka's producer and consumer functionality.
 * It manages the sending and receiving of rate data messages to and from the Kafka system.
 * The class provides a simple interface for producing and consuming rate data in the form of
 * RateDto objects.
 * <p>
 * It uses the EventProducer class to produce rate data to Kafka and the EventConsumer class to
 * consume rate data from Kafka. The Kafka class abstracts the communication details with the Kafka
 * brokers, allowing other components to focus on rate data without having to manage Kafka's low-level API.
 * </p>
 */
public class Kafka {
    private final EventConsumer consumer;
    private final EventProducer producer;

    /**
     * Constructs a new Kafka instance, initializing the consumer and producer.
     * This allows both consumption and production of rate data to/from Kafka.
     */
    public Kafka() {
        this.consumer = new EventConsumer();
        this.producer = new EventProducer();
    }

    /**
     * Sends a RateDto object to Kafka.
     * This method uses the EventProducer to send the rate data to the "rates" topic.
     *
     * @param dto the RateDto object containing the rate data to be sent
     */
    public void produceRate(RateDto dto) {
        producer.produceRate(dto);
    }

    /**
     * Consumes rate data from Kafka.
     * This method uses the EventConsumer to retrieve rate data from the "rates" topic.
     *
     * @return a list of RateDto objects representing the consumed rate data
     */
    public List<RateDto> consumeRate() {
        return consumer.consumeRate();
    }
}
