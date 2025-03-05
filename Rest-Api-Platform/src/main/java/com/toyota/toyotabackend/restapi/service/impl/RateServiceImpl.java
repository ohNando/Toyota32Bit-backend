package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.producer.RateProducer;
import com.toyota.toyotabackend.restapi.service.RateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateProducer rateProducer;

    @Override
    public RateDto getRate(String rateName) {
        if(!rateName.startsWith("PF2_")){
            throw new IllegalArgumentException("Invalid platform name!");
        }
        String currency = rateName.substring(4);
        Map<String,Double> rates = rateProducer.generateRates(currency);

        double bid = rates.get("bid");
        double ask = rates.get("ask");
        if(bid == 0 || ask == 0){
            throw new IllegalArgumentException("Rate can not found!" + rateName);
        }

        return new RateDto(rateName, bid, ask, LocalDateTime.now().toString());
    }
}
