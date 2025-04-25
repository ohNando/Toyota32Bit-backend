package com.toyota.toyotabackend.restapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RateNotFoundException extends RuntimeException {
    public RateNotFoundException(String rate) {
        super("Rate can not found: " + rate);
    }
}
