package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.entity.Rate;
import com.toyota.toyotabackend.restapi.parser.CommandParser;
import com.toyota.toyotabackend.restapi.producer.RateProducer;
import com.toyota.toyotabackend.restapi.service.RateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Service implementation for handling rate-related operations such as retrieving the rates for a given currency.
 * <p>
 * This service performs rate generation, validation of commands, and returns the rate data in the form of a {@link Rate}.
 * </p>
 */
@Service
public class RateServiceImpl implements RateService {

    /**
     * Retrieves the rate for the specified currency from the received message.
     *
     * @param receivedMessage The message containing the requested rate and other parameters.
     * @return A {@link ResponseEntity} containing a {@link Rate} with the rate data (bid, ask, and timestamp).
     */
    @Override
    public Rate getRate(String receivedMessage) {
        if(!CommandParser.checkCommand(receivedMessage)) {
            return null;
        }
        float[] rateParts = RateProducer.generateRates(receivedMessage);

        return new Rate(receivedMessage, rateParts[0], rateParts[1]);
    }
}
