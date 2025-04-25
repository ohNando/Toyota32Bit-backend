package com.toyota.toyotabackend.restapi.service;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service interface for handling rate retrieval and processing.
 * <p>
 * This service is responsible for retrieving currency rates, processing them,
 * and returning the rate details in the form of a {@link RateDto}.
 * </p>
 */
@Service
public interface RateService {

    /**
     * Retrieves the currency rate for a specified rate name.
     * <p>
     * This method checks the validity of the request, fetches the appropriate rate,
     * and returns a {@link ResponseEntity} containing a {@link RateDto} with the rate details.
     * </p>
     *
     * @param rateName The name of the currency rate to be retrieved.
     * @return A {@link ResponseEntity} containing the {@link RateDto} with the rate details.
     */
    ResponseEntity<RateDto> getRate(String rateName);
}
