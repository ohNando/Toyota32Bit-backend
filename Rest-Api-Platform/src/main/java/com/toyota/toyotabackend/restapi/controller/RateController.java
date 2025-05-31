package com.toyota.toyotabackend.restapi.controller;

import lombok.AllArgsConstructor;
import com.toyota.toyotabackend.restapi.entity.Rate;
import com.toyota.toyotabackend.restapi.service.RateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST controller that handles requests related to currency rates.
 * This class exposes an API endpoint to retrieve rate information for a given rate name.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/rates")
public class RateController {

    private final RateService rateService;

    /**
     * Endpoint to get the rate information for a specific rate name.
     * <p>
     * This method handles GET requests to retrieve rate details for a given currency pair
     * by invoking the {@link RateService#getRate(String)} method. The rate name is extracted
     * from the URL path and passed to the service.
     * </p>
     *
     * @param rateName The name of the currency pair whose rate is requested.
     * @return A {@link Rate} object containing the rate details.
     */
    @GetMapping(path = "/{rateName}")
    public Rate getRates(@PathVariable String rateName) {
        return rateService.getRate(rateName);
    }
}
