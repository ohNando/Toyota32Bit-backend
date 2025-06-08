package com.example.kafka.consumer;

import com.example.kafka.entity.Rate;
import com.example.kafka.mapper.RateMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;

public class RateDeserializer implements Deserializer<Rate> {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public Rate deserialize(String topic, byte[] data) {
        if(data == null) return null;
        String dataString = Arrays.toString(data);
        return RateMapper.stringToRate(dataString);
    }
}
