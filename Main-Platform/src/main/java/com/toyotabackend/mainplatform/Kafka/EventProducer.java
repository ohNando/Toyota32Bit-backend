package com.toyotabackend.mainplatform.Kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Mapper.RateMapper;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * The EventProducer class is responsible for producing messages to a Kafka topic.
 * It sends serialized RateDto objects to the "rates" Kafka topic, which can then
 * be consumed by other components in the system.
 * <p>
 * This class uses Kafka's producer API to send serialized messages to the topic.
 * The messages represent rate data, which are serialized from RateDto objects
 * using the RateMapper class.
 * </p>
 */
public class EventProducer {
    private final Logger logger = LogManager.getLogger(EventProducer.class);
    private String topic;  // The Kafka topic name to send messages to
    private final Producer<String, String> producer;

    /**
     * Constructs a new EventProducer instance with default Kafka properties.
     * The producer is configured to connect to a local Kafka broker at "localhost:9092"
     * and to send messages to the "rates" topic.
     */
    public EventProducer() {
        final Properties properties = new Properties() {
            {
                put(BOOTSTRAP_SERVERS_CONFIG, "kafka_broker:9092");
                put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
                put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
                put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start reading messages from the beginning
            }
        };
        this.producer = new KafkaProducer<>(properties);
        this.topic = "rates";  // The Kafka topic to send messages to
    }

    /**
     * Sends a RateDto object as a message to the Kafka topic.
     * The message key is the rate name, and the message value is the serialized RateDto.
     *
     * @param dto the RateDto object containing the rate data to be sent
     */
    public void produceRate(RateDto dto) {
        producer.send(new ProducerRecord<>(topic, dto.getRateName(), RateMapper.rateToString(dto)),
                (event, error) -> CallBack(event, error, topic, dto.getRateName(), dto));
    }

    /**
     * Callback method for handling the result of the Kafka message send operation.
     * If there is an error during message sending, it logs a warning. Otherwise, it logs
     * the successful production of the event.
     *
     * @param event the event produced
     * @param error the error, if any, during message production
     * @param topic the Kafka topic the message was sent to
     * @param rateName the rate name key of the produced message
     * @param dto the RateDto object that was sent
     */
    private void CallBack(Object event, Exception error, String topic, String rateName, RateDto dto) {
        if (error != null) {
            logger.warn(error.getMessage());
        } else {
            logger.info("Produced event to topic {}: key = %-10s value = {}", topic, dto.toString());
        }
    }
}
