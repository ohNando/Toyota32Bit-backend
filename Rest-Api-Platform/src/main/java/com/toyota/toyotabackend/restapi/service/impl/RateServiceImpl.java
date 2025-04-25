package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.exception.CommandNotValidException;
import com.toyota.toyotabackend.restapi.exception.RateNotFoundException;
import com.toyota.toyotabackend.restapi.parser.CommandParser;
import com.toyota.toyotabackend.restapi.producer.RateProducer;
import com.toyota.toyotabackend.restapi.service.RateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateProducer rateProducer;
    private final CommandParser parser;

    @Override
    public ResponseEntity<RateDto> getRate(String receivedMessage) {
        String checkedMessage = parser.checkCommand(receivedMessage);

        if(!checkedMessage.equals("OK")) {
            throw new CommandNotValidException(checkedMessage);
        }

        String rateName = parser.getRate(receivedMessage);
        Map<String,Double> rates = rateProducer.generateRates(rateName);

        if(Objects.isNull(rates) || !rates.containsKey("bid") || !rates.containsKey("ask")) {
            throw new RateNotFoundException("Rate not found: " + rateName);
        }

        double bid = rates.get("bid");
        double ask = rates.get("ask");

        return ResponseEntity.ok(new RateDto(rateName,bid,ask,LocalDateTime.now().toString()));
    }
}
