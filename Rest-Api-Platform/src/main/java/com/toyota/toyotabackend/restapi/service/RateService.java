package com.toyota.toyotabackend.restapi.service;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface RateService {
    ResponseEntity<RateDto> getRate(String rateName);
}
