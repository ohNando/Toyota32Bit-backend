package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.exception.CommandNotValidException;
import com.toyota.toyotabackend.restapi.exception.RateNotFoundException;
import com.toyota.toyotabackend.restapi.parser.CommandParser;
import com.toyota.toyotabackend.restapi.parser.impl.CommandParserImpl;
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
    public RateDto getRate(String receivedMessage) {
        CommandParser parser = new CommandParserImpl();
        String checkedMessage = parser.checkCommand(receivedMessage);

        if(checkedMessage.equals("OK")){
            String rateName = parser.getRate(receivedMessage);
            Map<String,Double> rates = rateProducer.generateRates(rateName);
            double bid = rates.get("bid");
            double ask = rates.get("ask");
            if(bid == 0 || ask == 0){
                throw new RateNotFoundException("Rate can not found!" + rateName);
            }
            return new RateDto(rateName, bid, ask, LocalDateTime.now().toString());
        }else{
            throw new CommandNotValidException(checkedMessage);
        }
    }
}
