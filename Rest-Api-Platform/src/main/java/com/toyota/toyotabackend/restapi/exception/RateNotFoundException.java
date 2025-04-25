package com.toyota.toyotabackend.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested currency rate cannot be found.
 * The {@link ResponseStatus} annotation automatically maps this exception
 * to a {@link HttpStatus#BAD_REQUEST} HTTP status response when thrown in a Spring MVC controller.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RateNotFoundException extends RuntimeException {

    /**
     * Constructs a new RateNotFoundException with a detailed message including the
     * rate that could not be found.
     *
     * @param rate the name of the rate that could not be found.
     */
    public RateNotFoundException(String rate) {
        super("Rate can not found: " + rate);
    }
}
