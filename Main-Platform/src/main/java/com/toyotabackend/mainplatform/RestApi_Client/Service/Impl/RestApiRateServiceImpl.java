package com.toyotabackend.mainplatform.RestApi_Client.Service.Impl;

import com.toyotabackend.mainplatform.RestApi_Client.Service.RestApiRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class RestApiRateServiceImpl implements RestApiRateService {
    private final RestTemplate restTemplate;
    private final String base_url = "http://localhost:8080/api/rates/";

    @Autowired
    public RestApiRateServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getRate(String rateType) {
        String url = base_url + rateType;
        return restTemplate.getForObject(url, String.class);
    }

}
