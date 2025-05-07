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

public class EventConsumer {
    private final Consumer<String,String> consumer;
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
        this.topic = "rates";
    }

    public List<RateDto> consumeRate(){
        List<RateDto> dtoList = new ArrayList<>();
        synchronized(consumer) {
            consumer.subscribe(Arrays.asList(topic));
            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000));
            for(ConsumerRecord<String,String> record : records){
                dtoList.add(RateMapper.stringToDTO(record.value()));
            }
        }
        return dtoList;
    }
}
