package com.toyotabackend.mainplatform.Client;

import com.toyotabackend.mainplatform.Coordinator.Coordinator;
import com.toyotabackend.mainplatform.Data_Provider.DataProvider;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.RateFields;
import com.toyotabackend.mainplatform.Entity.RateStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RestSubscriber extends Thread implements DataProvider {  //Subscriber 1
    Coordinator coordinator;
    @Value("${client.Rest_Api.baseUrl}")
    private String baseUrl;
    @Value("${client.Rest_Api.loginUrl}")
    private String loginUrl;
    private RestTemplate restTemplate;
    private Map<String, Boolean> subscriptionFlags = new ConcurrentHashMap<>();
    private Map<String, Thread> subscriptionThreads = new ConcurrentHashMap<>();

    Logger logger = LoggerFactory.getLogger(RestSubscriber.class);

    public RestSubscriber(Coordinator coordinator) {
        this.coordinator = coordinator;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Boolean connect(String platformName, String username, String password) {
        if(!platformName.equals("PF2")){
            logger.warn("Invalid platform name: " + platformName);
            return false;
        }
        String request = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
            String responseStatus = response.getStatusCode().toString();

            if(responseStatus.equals("200")){   //OK
                logger.info("Successfully connected to {} with username {}", baseUrl, username);
                return true;
            }else{
                logger.warn("Failed to connect to {} with username {}", baseUrl, username);
                return false;
            }
        }catch (Exception e){
            logger.warn("Failed to connect to {} with username {}", baseUrl, username);
            return false;
        }
    }

    @Override
    public Boolean disConnect(String platformName, String username, String password) {
        if(!platformName.equals("PF2")){
            logger.warn("Invalid platform name: " + platformName);
            return false;
        }
        logger.info("Disconnecting from {} with username {}", baseUrl, username);
        return true;
    }

    @Override
    public void subscribe(String platformName, String rateName) {
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: " + platformName);
            return;
        }

        String key = platformName + "_" + rateName;
        String url = baseUrl + key;

        subscriptionFlags.put(key, true);

        Thread thread = new Thread(this);
        subscriptionThreads.put(key, thread);

        thread.start();
    }

    @Override
    public void unSubscribe(String platformName, String rateName) {
        String key = platformName + "_" + rateName;
        subscriptionFlags.put(key, false);

        Thread thread = subscriptionThreads.get(key);
        if (thread != null) {
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                logger.warn("Failed to join subscription thread");
            }
            subscriptionThreads.remove(key);
        }
    }

    @Override
    public void run() {
        for (Map.Entry<String, Boolean> entry : subscriptionFlags.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue()) {
                String[] parts = key.split("_");
                String platformName = parts[0];
                String rateName = parts[1];
                String url = baseUrl + key;

                RateStatus status = RateStatus.AVAILABLE;

                while (subscriptionFlags.get(key)) {
                    try {
                        ResponseEntity<RateDto> response = restTemplate.getForEntity(url, RateDto.class);
                        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                            RateDto dto = response.getBody();
                            if (status == status.AVAILABLE) {
                                coordinator.onRateAvailable(platformName, rateName, dto);
                                status = status.UNAVAILABLE;
                                coordinator.onRateStatus(platformName, rateName, status);
                            } else {
                                coordinator.onRateUpdate(platformName, rateName, new RateFields(dto.getBid(), dto.getAsk(), dto.getRateUpdateTime()));
                                coordinator.onRateStatus(platformName, rateName, status);
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        logger.warn("Failed to connect to {} with username {}", baseUrl, key);
                    }
                }
                logger.info("Subscribed to {} with username {}", baseUrl, key);
            }
        }
    }
}
