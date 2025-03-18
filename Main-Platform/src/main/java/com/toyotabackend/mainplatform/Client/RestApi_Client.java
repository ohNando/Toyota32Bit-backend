package com.toyotabackend.mainplatform.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class RestApi_Client {
    private final RestTemplate restTemplate;
    @Value("${client.Rest_Api.baseUrl}")
    private String baseUrl;

    public RestApi_Client() {
        this.restTemplate = new RestTemplate();
    }

    public String getRate(String rateType){
        String url = baseUrl + rateType;
        return restTemplate.getForObject(url, String.class);
    }
}
