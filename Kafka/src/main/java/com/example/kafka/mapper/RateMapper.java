package com.example.kafka.mapper;

import com.example.kafka.entity.Rate;

import java.time.Instant;

public class RateMapper {
    public static Rate stringToRate(String stringRate) {
        Rate rate = new Rate();
        String[] rateFields = stringRate.split("\\|");

        rate.setRateName(rateFields[0]);
        rate.setBid(Float.parseFloat(rateFields[1]));
        rate.setAsk(Float.parseFloat(rateFields[2]));
        rate.setRateUpdateTime(Instant.parse(rateFields[3]));
        return rate;
    }
}
