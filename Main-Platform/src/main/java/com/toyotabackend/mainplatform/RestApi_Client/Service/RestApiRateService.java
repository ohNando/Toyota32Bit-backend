package com.toyotabackend.mainplatform.RestApi_Client.Service;

import org.springframework.stereotype.Service;

@Service
public interface RestApiRateService {
    public String getRate(String rateType);
}
