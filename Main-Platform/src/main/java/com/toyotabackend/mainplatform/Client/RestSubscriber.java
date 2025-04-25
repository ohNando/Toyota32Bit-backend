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

/**
 * RestSubscriber is a data provider implementation that fetches rate information
 * from a REST API based on a subscription model. It runs in a separate thread and
 * repeatedly polls the API for updates for subscribed rate names.
 *
 * This class is specifically designed to work with platform "PF2".
 */
public class RestSubscriber extends Thread implements DataProvider {

    private final Coordinator coordinator;
    private final RestTemplate restTemplate;
    private final Map<String, Boolean> subscriptionFlags = new ConcurrentHashMap<>();
    private final Map<String, Thread> subscriptionThreads = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(RestSubscriber.class);

    @Value("${client.Rest_Api.baseUrl}")
    private String baseUrl;

    @Value("${client.Rest_Api.loginUrl}")
    private String loginUrl;

    /**
     * Constructs a new RestSubscriber with a reference to the Coordinator
     * to receive rate updates and status notifications.
     *
     * @param coordinator the coordinator that handles callbacks
     */
    public RestSubscriber(Coordinator coordinator) {
        this.coordinator = coordinator;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Attempts to authenticate the subscriber with the REST API using the provided
     * platform name, username, and password.
     *
     * @param platformName the platform identifier (must be "PF2")
     * @param username     the username for login
     * @param password     the password for login
     * @return true if login is successful, false otherwise
     */
    @Override
    public Boolean connect(String platformName, String username, String password) {
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            return false;
        }

        String request = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully connected to {} with username {}", baseUrl, username);
                return true;
            } else {
                logger.warn("Failed to connect to {} with username {}", baseUrl, username);
                return false;
            }
        } catch (Exception e) {
            logger.warn("Failed to connect to {} with username {}", baseUrl, username);
            return false;
        }
    }

    /**
     * Disconnects from the platform. Currently, this method just logs the disconnection.
     *
     * @param platformName the platform identifier
     * @param username     the username
     * @param password     the password
     * @return true always, unless platform name is invalid
     */
    @Override
    public Boolean disConnect(String platformName, String username, String password) {
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            return false;
        }

        logger.info("Disconnecting from {} with username {}", baseUrl, username);
        return true;
    }

    /**
     * Subscribes to rate updates for the specified platform and rate name.
     * Starts a new thread to fetch data continuously.
     *
     * @param platformName the platform identifier
     * @param rateName     the rate name to subscribe to
     */
    @Override
    public void subscribe(String platformName, String rateName) {
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            return;
        }

        String key = platformName + "_" + rateName;
        subscriptionFlags.put(key, true);

        Thread thread = new Thread(this);
        subscriptionThreads.put(key, thread);

        thread.start();
    }

    /**
     * Unsubscribes from a specific rate by setting its subscription flag to false
     * and interrupting the associated thread.
     *
     * @param platformName the platform identifier
     * @param rateName     the rate name to unsubscribe from
     */
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

    /**
     * The main run loop that performs polling of rate data for all active subscriptions.
     * It sends rate updates and availability status to the coordinator.
     */
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
                            if (status == RateStatus.AVAILABLE) {
                                coordinator.onRateAvailable(platformName, rateName, dto);
                                status = RateStatus.UNAVAILABLE;
                                coordinator.onRateStatus(platformName, rateName, status);
                            } else {
                                coordinator.onRateUpdate(platformName, rateName,
                                        new RateFields(dto.getBid(), dto.getAsk(), dto.getRateUpdateTime()));
                                coordinator.onRateStatus(platformName, rateName, status);
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        logger.warn("Failed to connect to {} for rate {}", baseUrl, key);
                    }
                }
                logger.info("Unsubscribed from {} for rate {}", baseUrl, key);
            }
        }
    }
}