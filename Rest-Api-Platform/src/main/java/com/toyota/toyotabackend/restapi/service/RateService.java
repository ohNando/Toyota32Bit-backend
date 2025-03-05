package com.toyota.toyotabackend.restapi.service;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import org.springframework.stereotype.Service;

@Service
public interface RateService {
    RateDto getRate(String rateName);
}
