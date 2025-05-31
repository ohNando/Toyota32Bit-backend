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

/**
 * Service implementation for handling rate-related operations such as retrieving the rates for a given currency.
 * <p>
 * This service performs rate generation, validation of commands, and returns the rate data in the form of a {@link RateDto}.
 * </p>
 */
@Service
@AllArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateProducer rateProducer;
    private final CommandParser parser;

    /**
     * Retrieves the rate for the specified currency from the received message.
     *
     * @param receivedMessage The message containing the requested rate and other parameters.
     * @return A {@link ResponseEntity} containing a {@link RateDto} with the rate data (bid, ask, and timestamp).
     * @throws CommandNotValidException If the command is invalid (e.g., missing or incorrect rate).
     * @throws RateNotFoundException If the rate cannot be found for the specified currency.
     */
    @Override
    public ResponseEntity<RateDto> getRate(String receivedMessage) {
        if(!parser.checkCommand(receivedMessage)) {
            return null;
        }

        Map<String,Double> rates = rateProducer.generateRates(receivedMessage);
        if(Objects.isNull(rates) || !rates.containsKey("bid") || !rates.containsKey("ask")) {
            throw new RateNotFoundException("Rate not found: " + receivedMessage);
        }

        double bid = rates.get("bid");
        double ask = rates.get("ask");

        return ResponseEntity.ok(new RateDto(receivedMessage, bid, ask, LocalDateTime.now().toString()));
    }
}
