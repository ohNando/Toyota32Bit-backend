package com.example.kafka.consumer;

import com.example.kafka.entity.Rate;
import com.example.kafka.mapper.RateMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

public class EventConsumer {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;

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

    public List<Rate> consumeRate(){
        List<Rate> rateList = new ArrayList<>();
        synchronized (consumer) {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String, String> record : records){
                rateList.add(RateMapper.stringToRate(record.value()));
            }
        }
        return rateList;
    }
}
