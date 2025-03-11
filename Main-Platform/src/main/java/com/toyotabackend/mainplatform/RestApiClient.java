package com.toyotabackend.mainplatform;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class RestApiClient {
    private final HttpClient client;
    private final String BASE_URL = "http://localhost:8082/api/rates/PF2_USDTRY";

    public RestApiClient() {
        client = HttpClient.newHttpClient();
    }

    public String getRate() throws IOException, InterruptedException {
        System.out.println("getRate metodu cagirildi!!");
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Gelen veri:"+response);
        return response.body();
    }
}
