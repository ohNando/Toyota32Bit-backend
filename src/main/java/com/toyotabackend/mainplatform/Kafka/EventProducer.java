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

public class EventProducer{
    private final Logger logger = LogManager.getLogger("KafkaLogger");
    private String topic;  // The Kafka topic name to send messages to
    private final Producer<String,String> producer;

    public EventProducer(){
        final Properties properties = new Properties(){
            {
                put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
                put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
                put(AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read all messages from the beginning
            }
        };
        this.producer = new KafkaProducer<>(properties);
        this.topic = "rates";
    }

    public void produceRate(RateDto dto){
        producer.send(new ProducerRecord<>(topic,dto.getRateName(),RateMapper.rateToString(dto)),
                                    (event,error) -> CallBack(event,error,topic,dto.getRateName(),dto));
    }

    private void CallBack(Object event , Exception error,String topic,String rateName,RateDto dto){
        if(error != null){
            logger.warn(error.getMessage());
        }else{
            logger.info("Produced event to topic %s: key = %-10s value = %s%n", topic, rateName, dto.toString());
        }
    }

}
