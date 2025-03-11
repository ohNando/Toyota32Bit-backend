package com.toyotabackend.mainplatform;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ClientController {
    private final RestApiClient restApiClient;
    public ClientController(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    @GetMapping("/PF2_USDTRY")
    public String getRate() throws IOException, InterruptedException {
        System.out.println("getRate calisti!!!");
        String response = restApiClient.getRate();
        System.out.println(response);
        return response;
    }

}
