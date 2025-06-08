package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.AuthEntity.LoginEntity;
import com.toyotabackend.mainplatform.AuthEntity.UserAuth;
import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Config.AppConfig;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;

import lombok.Getter;
import lombok.Setter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * RestSubscriber is a data provider implementation that fetches rate information
 * from a REST API based on a subscription model. It runs in a separate thread and
 * repeatedly polls the API for updates for subscribed rate names.
 * This class is specifically designed to work with platform "PF2".
 */
@Getter
@Setter
public class RestSubscriber extends Thread implements SubscriberInterface {
    private CoordinatorInterface coordinator;
    private final List<String> subscribedRates;
    private final RestTemplate restTemplate;

    private String subscriberName;
    private String baseUrl;
    private String loginUrl;
    private boolean connectionStatus;

    private final Logger logger = LogManager.getLogger("SubscriberLogger");

    /**
     * Constructs a new RestSubscriber with a reference to the Coordinator
     * to receive rate updates and status notifications.
     *
     * @param _subscriberName name of the subscriber
     * @param baseUrl url of the Rest endpoint
     */
    public RestSubscriber(String _subscriberName,String baseUrl) throws IOException{
        logger.info("initialializing Subscriber {}", subscriberName);
        
        this.subscriberName = _subscriberName;
        this.baseUrl = baseUrl;
        this.loginUrl = AppConfig.getRESTLoginUrl();

        this.restTemplate = new RestTemplate();
        this.setConnectionStatus(false);
        this.subscribedRates = new ArrayList<>();
        logger.info("Subscriber initialized {}", subscriberName);
    }

    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }
    @Override
    public boolean getConnectionStatus(){
        return this.connectionStatus; 
    }

    @Override
    public boolean checkPlatformName(String platformName){
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            this.connectionStatus = false;
            return false ;
        }
        return true;
    }

    /**
     * Attempts to authenticate the subscriber with the REST API using the provided
     * platform name, username, and password.
     *
     * @param platformName the platform identifier (must be "PF2")
     * @param username     the username for login
     * @param password     the password for login
     */
    @Override
    public void connect(String platformName, String username, String password) {
        if(!checkPlatformName(platformName)) return;

        logger.info("Connecting to {}",this.loginUrl);
        LoginEntity login = new LoginEntity(username,password);

        try {
            ResponseEntity<UserAuth> response = restTemplate.postForEntity(this.loginUrl, login, UserAuth.class);

            if(response.getStatusCode() != HttpStatus.OK){
                logger.warn("Failed to connect to : {} with status code: {}", this.loginUrl, response.getStatusCode());
                this.setConnectionStatus(false);
                return;
            }
            logger.info("Connected to {} , status code: {}",this.loginUrl,response.getStatusCode());
            
            
            UserAuth loginResponse = response.getBody();

            if (loginResponse.getStatus().equals("success")) {
                logger.info("Successfully connected to {} with username {}", baseUrl, username);
            } else {
                logger.warn("Failed to login to {} with username {}", baseUrl, username);
                this.setConnectionStatus(false);
                return;
            }

            this.setConnectionStatus(true); 
            coordinator.onConnect(platformName, connectionStatus);
            logger.info("Connected to {}",baseUrl);
            this.start();

        } catch (Exception e) {
            this.setConnectionStatus(false);
            logger.warn("Failed to connect to {} with username {}", baseUrl, username);
        }
    }

    /**
     * Disconnects from the platform. Currently, this method logs for the disconnection
     * and call the onDisconnect method.
     *
     * @param platformName the platform identifier
     * @param username     the username
     * @param password     the password
     */
    @Override
    public void disConnect(String platformName, String username, String password){
        if(!checkPlatformName(platformName)) return;

        logger.info("Disconnecting from {}", baseUrl);
        this.setConnectionStatus(false);
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
        if(!checkPlatformName(platformName)) return;

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
        if(!checkPlatformName(platformName)) return;

        logger.info("{} is unsubscribing from {}",platformName,rateName);
        String key = platformName + "_" + rateName;
        subscribedRates.remove(key);
            logger.info("{} is unsubscribed from {}",platformName,rateName);
    }

    /**
     * The main run loop that performs polling of rate data for all active subscriptions.
     * It sends rate updates and availability status to the coordinator.
     */
    @Override
    public void run() {
        logger.info("Subscriber is starting.");
        while(connectionStatus){
            for(String rateName : subscribedRates){
                String requestURL = baseUrl + "/" + rateName;
                ResponseEntity<RateDto> response = null;
                
                int countOfTry = 0;
                boolean successful = false;
                while(countOfTry < 3 && !successful){
                    try{
                        response = restTemplate.getForEntity(requestURL, RateDto.class);
                        if(response.getBody() != null && response.getBody().getRateUpdateTime() == null){
                            logger.debug("RateUpdateTime for {} was null , set to current time.",
                                    response.getBody().getRateUpdateTime());
                            response.getBody().setRateUpdateTime(Instant.now());
                        }
                        logger.info("DTO INFO (from rest api)= {} {} {} Time = {}",
                                response.getBody().getRateName(),
                                response.getBody().getBid(),
                                response.getBody().getAsk(),
                                response.getBody().getRateUpdateTime());
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
                    logger.error("Unable to get data for {} after 3 retries",rateName);
                    continue;
                }

                RateDto dto = response.getBody();
                switch (coordinator.onRateStatus(this.subscriberName,dto.getRateName())) {
                    case RateStatus.NOT_AVAILABLE:
                        logger.info("Fetched rate in NOT_AVAILABLE: {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());
                        coordinator.onRateAvailable(this.subscriberName, dto.getRateName(), dto);
                        break;
                    case RateStatus.AVAILABLE:
                        logger.info("Fetched rate in AVAILABLE: {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());
                        coordinator.onRateUpdate(this.subscriberName, dto.getRateName(), dto);
                        break;
                    case RateStatus.UPDATED:
                        logger.info("Fetched rate in UPDATED: {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());
                        coordinator.onRateUpdate(this.subscriberName, dto.getRateName(), dto);
                        break;
                }
            }

            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                logger.error("PF2 subscriber Error : {}",e.getMessage());
                this.connectionStatus = false;
            }
        }
    }
}