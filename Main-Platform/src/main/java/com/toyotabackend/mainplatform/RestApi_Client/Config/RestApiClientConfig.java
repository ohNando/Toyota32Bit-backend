package com.toyotabackend.mainplatform.RestApi_Client.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestApiClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
