package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.AuthEntity.LoginEntity;
import com.toyotabackend.mainplatform.AuthEntity.UserAuth;
import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RestSubscriber is a data provider implementation that fetches rate information
 * from a REST API based on a subscription model. It runs in a separate thread and
 * repeatedly polls the API for updates for subscribed rate names.
 *
 * This class is specifically designed to work with platform "PF2".
 */
public class RestSubscriber extends Thread implements SubscriberInterface {

    private CoordinatorInterface coordinator;
    private boolean connectionStatus;
    private final List<String> subscribedRates;
    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger("SubscriberLogger");

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
    public RestSubscriber(){
        this.restTemplate = new RestTemplate();
        this.connectionStatus = false;
        this.subscribedRates = new ArrayList<>();
        logger.info("Subscriber initialialized");
    }

    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
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
    public void connect(String platformName, String username, String password) {
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            this.connectionStatus = false;
            return;
        }
        logger.info("Connecting to {}",baseUrl);
        LoginEntity login = new LoginEntity(username,password);

        try {
            ResponseEntity<UserAuth> response = restTemplate.postForEntity(this.loginUrl, login, UserAuth.class);

            if(response.getStatusCode() != HttpStatus.OK){
                logger.warn("Failed to connect to : {} with status code: {}", baseUrl, response.getStatusCode());
                this.connectionStatus = false;
                return;
            }
            
            UserAuth loginResponse = response.getBody();

            if (loginResponse.getStatus().equals("OK")) {
                logger.info("Successfully connected to {} with username {}", baseUrl, username);
            } else {
                logger.warn("Failed to connect to {} with username {}", baseUrl, username);
                this.connectionStatus = false;
                return;
            }

            this.connectionStatus = true;
            coordinator.onConnect(platformName, connectionStatus);
            logger.info("Connected to {}",baseUrl);
            this.start();

        } catch (Exception e) {
            logger.warn("Failed to connect to {} with username {}", baseUrl, username);
            return;
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
    public void disConnect(String platformName, String username, String password){
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            this.connectionStatus = false;
            return;
        }
        logger.info("Disconnecting from {}", baseUrl);
        this.connectionStatus = false;
        coordinator.onDisConnect(platformName,connectionStatus);
        logger.info("Disconnected from {}", baseUrl);
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
        logger.info("{} is subscribing to {}",platformName,rateName);
        subscribedRates.add(platformName + "_" + rateName);
        logger.info("{} subscribed to {}",platformName,rateName);
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
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            return;
        }

        logger.info("{} is unsubscribing from {}",platformName,rateName);
        String key = platformName + "_" + rateName;
        if(subscribedRates.contains(key))
            subscribedRates.remove(key);
            logger.info("{} is unsubscribed from {}",platformName,rateName);
    }

    /**
     * The main run loop that performs polling of rate data for all active subscriptions.
     * It sends rate updates and availability status to the coordinator.
     */
    @Override
    public void run() {
        while(connectionStatus){
            for(String rateName : subscribedRates){
                String requestURL = baseUrl + "/" + rateName;
                ResponseEntity<RateDto> response = null;

                int countOfTry = 0;
                boolean successful = false; 
                while(countOfTry < 5 & !successful){
                    try{
                        response = restTemplate.getForEntity(requestURL, RateDto.class);
                        if(response.getStatusCode() == HttpStatus.OK){
                            successful = true;
                        }else{
                            logger.error("Failed to get rate {}, Status Code: {}",rateName,response.getStatusCode());
                        }
                    }catch(ResourceAccessException error){
                        logger.error("Connection issue. Error: {}",error.getMessage());
                    }catch(Exception error){
                        logger.error("Unexpected error. Error : {}",error.getMessage());
                    }

                    if(!successful){
                        countOfTry++;
                        try{
                            Thread.sleep(1000);
                        }catch(InterruptedException error){
                            logger.error("Subscriber error: {}",error.getMessage());
                            throw new RuntimeException(error);
                        }
                    }
                }

                if(!successful){
                    logger.error("Unable to get data for {} after 5 retries",rateName);
                    continue;
                }

                RateDto dto = response.getBody();
                switch (coordinator.onRateStatus("PF2",dto.getRateName())) {
                    case RateStatus.NOT_AVAILABLE:
                        coordinator.onRateAvailable("PF2", dto.getRateName(), dto);
                        break;
                    case RateStatus.AVAILABLE:
                        coordinator.onRateUpdate("PF2", dto.getRateName(), dto);
                        break;
                    case RateStatus.UPDATED:
                        coordinator.onRateUpdate("PF2", dto.getRateName(), dto);
                        break;
                }


            }

            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                logger.error("PF2 subscriber Error : {}",e.getMessage());
            }
        }
    }
}