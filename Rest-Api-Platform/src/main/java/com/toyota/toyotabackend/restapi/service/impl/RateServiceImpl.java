package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.entity.Rate;
import com.toyota.toyotabackend.restapi.list.RateList;
import com.toyota.toyotabackend.restapi.mapper.RateMapper;
import com.toyota.toyotabackend.restapi.service.RateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling rate-related operations such as retrieving the rates for a given currency.
 * <p>
 * This service performs rate generation, validation of commands, and returns the rate data in the form of a {@link Rate}.
 * </p>
 */
@Service
public class RateServiceImpl implements RateService {
    @Autowired
    private RateList rateList;

    /**
     * Retrieves the rate for the specified currency from the received message.
     *
     * @param receivedMessage The message containing the requested rate and other parameters.
     * @return A {@link ResponseEntity} containing a {@link Rate} with the rate data (bid, ask, and timestamp).
     */
    @Override
    public RateDto getRate(String receivedMessage) {

        Rate rate = rateList.getRate(receivedMessage);
        System.out.println(receivedMessage);
        RateDto dto = RateMapper.rateToDto(rate);
        return dto;
    }
}
