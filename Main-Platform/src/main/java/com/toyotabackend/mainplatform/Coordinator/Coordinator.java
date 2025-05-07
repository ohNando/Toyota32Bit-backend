package com.toyotabackend.mainplatform.Coordinator;

import com.toyotabackend.mainplatform.ClassLoader.LoadSubscriberClass;
import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Kafka.Kafka;
import com.toyotabackend.mainplatform.RateCalculator.RateCalculatorService;
import com.toyotabackend.mainplatform.RateService.DatabaseService;
import com.toyotabackend.mainplatform.RateService.RateService;
import com.toyotabackend.mainplatform.Cache.HazelcastCache;

import org.apache.catalina.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Coordinator is the main orchestrator of the data collection system.
 * It manages the lifecycle of data providers, processes rate data,
 * handles callbacks, sends data to Kafka, and stores it in Hazelcast cache.
 */

public class Coordinator extends Thread implements CoordinatorInterface, AutoCloseable {
    @Value("${rate.rawRates}")
    private String[] rawRateNames;
    private String[] calculatedRateNames;
    private String[] subscribedRateNames;
    private String[] subscriberNames;

    private final HashMap<String,RateStatus> rateStatusMap;
    private final HashMap<String, SubscriberInterface> subscriberMap;

    private final HazelcastCache rateCache;
    private final Kafka kafka;
    private final DatabaseService database;
    private static final Logger logger = LoggerFactory.getLogger("CoordinatorLogger");

    private final RateCalculatorService calculator;
    private final RateService rateService;

    @Value("${login.username}")
    private String username;
    
    @Value("${login.password}")
    private String password;

    /**
     * Constructs the Coordinator with required Kafka producer and consumer.
     *
     * @param kafkaConsumer the Kafka consumer to listen to messages
     * @param kafkaProducer the Kafka producer to send rate data
     * @throws Exception in case of initialization errors
     */
    public Coordinator(ApplicationContext applicationContext) throws Exception {
       logger.info("Initializing coordinator");
    }

    /**
     * Initializes the system by dynamically loading subscriber classes using reflection.
     *
     * @throws Exception if the classes cannot be loaded
     */

    /**
     * Callback indicating a successful connection to a platform.
     *
     * @param platformName the name of the connected platform
     * @param status       true if successful
     */
    @Override
    public void onConnect(String platformName, Boolean status) {
        logger.info("Connected to platform: {}", platformName);
    }

    /**
     * Callback indicating a disconnection from a platform.
     *
     * @param platformName the name of the disconnected platform
     * @param status       true if disconnected
     */
    @Override
    public void onDisConnect(String platformName, Boolean status) {
        logger.warn("Disconnected from platform: {}", platformName);
    }

    /**
     * Callback triggered when new rate data becomes available for the first time.
     * Sends the data to Kafka and caches it.
     *
     * @param platformName the platform name
     * @param rateName     the name of the rate
     * @param dto          the rate data
     */
    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto dto) {
        try {
            kafkaProducer.send(platformName, rateName, dto);
            cacheService.cacheRate(platformName + "_" + rateName, dto);
            logger.debug("Rate data sent to Kafka - Platform: {}, Rate: {}, Data: {}", platformName, rateName, dto);
        } catch (Exception e) {
            logger.error("Error while sending rate to Kafka - Platform: {}, Rate: {}", platformName, rateName, e);
        }
    }

    /**
     * Callback triggered when rate data is updated.
     * Converts it to a DTO, sends to Kafka, and updates the cache.
     *
     * @param platformName the platform name
     * @param rateName     the name of the rate
     * @param rateFields   the updated rate fields
     */
    @Override
    public void onRateUpdate(String platformName, String rateName, RateFields rateFields) throws JsonProcessingException {
        RateDto dto = new RateDto();
        dto.setRateName(rateName);
        dto.setBid(rateFields.getBid());
        dto.setAsk(rateFields.getAsk());
        dto.setRateUpdateTime(rateFields.getTimestamp());

        kafkaProducer.send(platformName, rateName, dto);
        cacheService.cacheRate(platformName + "_" + rateName, dto);
        logger.debug("Rate updated - Platform: {}, Rate: {}, Fields: {}", platformName, rateName, rateFields);
    }

    /**
     * Callback triggered when the rate status changes (e.g., AVAILABLE or UNAVAILABLE).
     *
     * @param platformName the platform name
     * @param rateName     the rate name
     * @param rateStatus   the new status
     */
    @Override
    public void onRateStatus(String platformName, String rateName, RateStatus rateStatus) {
        logger.debug("Rate status updated - Platform: {}, Rate: {}, Status: {}", platformName, rateName, rateStatus);
    }

    /**
     * Starts subscription for the given platform and all defined rates.
     *
     * @param platformName the platform to subscribe to
     * @throws IOException if subscription fails
     */
    private void subscriber(String platformName) throws IOException {
        logger.info("Starting subscription for platform: {}", platformName);
        for (DataProvider dataProvider : dataProviders) {
            for (String rateName : rateNames) {
                try {
                    dataProvider.subscribe(platformName, rateName);
                    logger.info("Subscribed -> Platform: {}, Rate: {}", platformName, rateName);
                } catch (Exception e) {
                    logger.error("Subscription error - Platform: {}, Rate: {}", platformName, rateName, e);
                }
            }
        }
    }

    /**
     * Runs the coordinator thread. It starts the subscriptions and listens to Kafka.
     */
    @Override
    public void run() {
        logger.info("Coordinator thread running.");
        try {
            subscriber("PF1");
            subscriber("PF2");
            kafkaConsumer.listen("rates");
        } catch (IOException e) {
            logger.error("Error occurred while running Coordinator thread", e);
        }
    }
}