package com.toyota.toyotabackend.restapi.controller;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.service.RateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A REST controller that handles requests related to currency rates.
 * This class exposes an API endpoint to retrieve rate information for a given rate name.
 */
@RestController
@RequestMapping("/api/rates")
public class RateController {
    @Autowired
    private RateService rateService;

    /**
     * Endpoint to get the rate information for a specific rate name.
     *
     * @param rateName The name of the currency pair whose rate is requested.
     * @return A {@link RateDto} object containing the rate details.
     */
    @GetMapping(path = "/{rateName}")
    public ResponseEntity<RateDto> getRates(@PathVariable String rateName) {
        RateDto dto = rateService.getRate(rateName);
        if(dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}