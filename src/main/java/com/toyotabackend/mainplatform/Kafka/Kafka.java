package com.toyotabackend.mainplatform.Kafka;

import java.util.List;

import com.toyotabackend.mainplatform.Dto.RateDto;

public class Kafka {
    private final EventConsumer consumer;
    private final EventProducer producer;

    public Kafka(){
        this.consumer = new EventConsumer();
        this.producer = new EventProducer();
    }

    public void produceRate(RateDto dto){
        producer.produceRate(dto);
    }

    public List<RateDto> consumeRate(){
        return consumer.consumeRate();
    }
}
