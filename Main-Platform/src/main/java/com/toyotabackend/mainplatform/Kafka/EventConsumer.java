package com.toyotabackend.mainplatform.Kafka;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Mapper.RateMapper;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

import java.time.Duration;
import java.util.*;

/**
 * The EventConsumer class is responsible for consuming messages from a Kafka topic.
 * It listens to the "rates" topic and maps the consumed messages into RateDto objects.
 * <p>
 * This class uses Kafka's consumer API to subscribe to a topic and fetch messages from Kafka.
 * The consumed messages are deserialized into RateDto objects, which are then processed or forwarded
 * for further processing in the application.
 * </p>
 */
public class EventConsumer {
    private final Consumer<String,String> consumer;
    private final String topic;
    
    /**
     * Constructs a new EventConsumer instance with default Kafka properties.
     * The consumer is configured to connect to a local Kafka broker at "localhost:9092"
     * and will start reading messages from the earliest offset.
     */
    public EventConsumer(){
        final Properties properties = new Properties(){
            {
                put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                put(KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getCanonicalName());
                put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
                put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read all messages from beginning
                put(GROUP_ID_CONFIG, "kafka-java-getting-started");
            }
        };
        consumer = new KafkaConsumer<>(properties);
        this.topic = "rates"; // The topic to consume from
    }

    /**
     * Consumes messages from the "rates" Kafka topic and returns a list of RateDto objects.
     * The messages are deserialized from JSON strings into RateDto instances using the RateMapper.
     *
     * @return A list of RateDto objects representing the consumed rate data
     */
    public List<RateDto> consumeRate(){
        List<RateDto> dtoList = new ArrayList<>();
        synchronized(consumer) {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000)); // Poll for records
            for(ConsumerRecord<String,String> record : records){
                // Map the consumed message to a RateDto
                dtoList.add(RateMapper.stringToDTO(record.value()));
            }
        }
        return dtoList;
    }
}
