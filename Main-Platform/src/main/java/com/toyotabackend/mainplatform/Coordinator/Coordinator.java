package com.toyotabackend.mainplatform.Coordinator;

import com.toyotabackend.mainplatform.ClassLoader.LoadSubscriberClass;
import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Config.AppConfig;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Kafka.Kafka;
import com.toyotabackend.mainplatform.RateCalculator.RateCalculatorService;
import com.toyotabackend.mainplatform.RateService.RateService;
import com.toyotabackend.mainplatform.Cache.HazelcastCache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Coordinator class orchestrates the data collection process in the backend system.
 * It is responsible for managing the lifecycle of subscribers, processing rate data,
 * handling connection and disconnection events, sending data to Kafka, and storing data
 * in the Hazelcast cache.
 * <p>
 * The Coordinator acts as a central component that connects to different platforms, collects rate data,
 * and processes it for use in various services like Kafka and database updates.
 * </p>
 */
public class Coordinator extends Thread implements CoordinatorInterface, AutoCloseable {
    private ApplicationContext applicationContext;

    private String[] rawRateNames;
    private String[] calculatedRateNames;
    private String[] subscribedRateNames;
    private String[] subscriberNames;

    private final HashMap<String, RateStatus> rateStatusMap;
    private final HashMap<String, SubscriberInterface> subscriberMap;

    private final HazelcastCache rateCache;
    private final Kafka kafka;
    private static final Logger logger = LogManager.getLogger(Coordinator.class);

    private final RateCalculatorService calculator;
    private final RateService rateService;

    private final String username, password;

    /**
     * Constructs a new Coordinator instance and initializes the system by setting up required services,
     * loading subscribers, and connecting to the platforms.
     *
     * @param applicationContext The Spring application context to access required beans
     * @throws IOException If there are issues loading properties or subscribers
     */
    public Coordinator(ApplicationContext applicationContext) throws IOException {
        logger.info("Initializing coordinator");
        this.applicationContext = applicationContext;

        // Load configuration from the application properties
        this.subscriberNames = AppConfig.getSubscriberNames();
        this.subscribedRateNames = AppConfig.getRawRates();
        this.rawRateNames = AppConfig.getRawRates();
        this.calculatedRateNames = AppConfig.getCalculatedRates();

        // Initialize data structures and services
        this.rateStatusMap = new HashMap<>();
        for (String rawRate : rawRateNames) {
            for (String subscriberName : subscriberNames) {
                logger.info("STATUS -- Subscriber name: " + subscriberName);
                logger.info("STATUS -- Rate name: " + rawRate);
                rateStatusMap.put(subscriberName + "_" + rawRate, RateStatus.NOT_AVAILABLE);
            }
        }

        this.subscriberMap = new HashMap<>();
        this.kafka = new Kafka();
        this.rateCache = new HazelcastCache();
        this.rateService = new RateService(this.rateCache);
        this.calculator = new RateCalculatorService(this.rateService, this.rawRateNames, AppConfig.getDerivedRates());

        // Load and initialize TCP and REST subscribers
        this.TCPLoader();
        this.RestLoader();

        // Load user credentials and connect to platforms
        this.username = AppConfig.getUsername();
        this.password = AppConfig.getPassword();
        this.Connector(this.username, this.password);

        logger.info("Coordinator initialized");
        this.start();
    }

    /**
     * Loads and registers the TCP subscriber by dynamically loading the subscriber class.
     *
     * @throws IOException If there is an issue loading the TCP subscriber configuration
     */
    private void TCPLoader() throws IOException {
        String subscriberName = AppConfig.getTCPSubscriberName();
        String serverAddress = AppConfig.getTCPAddress();
        int serverPort = AppConfig.getTCPPort();
        String classPath = AppConfig.getTCPClassPath();

        logger.info("Registering TCP subscriber: " + subscriberName);
        SubscriberInterface sub = LoadSubscriberClass.loadSubscriber(
            classPath,
            new Class<?>[]{String.class, String.class, int.class},
            new Object[]{subscriberName, serverAddress, serverPort}
        );

        subscriberMap.put(subscriberName, sub);
        sub.setCoordinator(this);
    }

    /**
     * Loads and registers the REST subscriber by dynamically loading the subscriber class.
     *
     * @throws IOException If there is an issue loading the REST subscriber configuration
     */
    private void RestLoader() throws IOException {
        String subscriberName = AppConfig.getRESTSubscriberName();
        String baseURL = AppConfig.getRESTBaseUrl();
        String classPath = AppConfig.getRESTClassPath();

        logger.info("Registering REST subscriber: " + subscriberName);
        try{
            SubscriberInterface sub = LoadSubscriberClass.loadSubscriber(
            classPath,
            new Class<?>[]{String.class, String.class},
            new Object[]{subscriberName, baseURL}
        );

        subscriberMap.put(subscriberName, sub);
        sub.setCoordinator(this);
        logger.info("REST subscriber {} successfully loaded", subscriberName);
        }catch(Exception e){
            logger.error("Error loading REST subscriber: " + e.getMessage(), e);
            throw new IOException("Error loading REST subscriber", e);
        }
        
    }

    /**
     * Connects to all subscriber platforms using the provided username and password.
     *
     * @param username The username for connecting to the platforms
     * @param password The password for connecting to the platforms
     */
    private void Connector(String username, String password) {
        try {
            for (String subscriberName : subscriberNames) {
                SubscriberInterface sub = this.subscriberMap.get(subscriberName);
                if (sub == null) {
                    logger.warn("Subscriber {} not found!", subscriberName);
                    throw new IllegalStateException("Failed to get subscriber" + subscriberName);
                }
                sub.connect(subscriberName, username, password);
                if (!sub.getConnectionStatus()) {
                    logger.warn(subscriberName + " couldn't connect");
                    subscriberMap.remove(subscriberName);
                }else{
                    logger.info("{} connected successfully", subscriberName);
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down the coordinator, disconnects all subscribers, and closes resources.
     */
    @Override
    public void close() {
        logger.info("Program is closing");
        for (String subscriberName : subscriberMap.keySet()) {
            SubscriberInterface sub = this.subscriberMap.get(subscriberName);
            sub.disConnect(subscriberName, username, password);
            subscriberMap.remove(subscriberName);
        }

        rateCache.closeCache();
        logger.info("Program is shut down");
    }

    /**
     * The main execution loop for the Coordinator, continuously processing rate data and updating Kafka and database.
     */
    @Override
    public void run() {
        logger.info("Coordinator is running");
        while (!subscriberMap.isEmpty()) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (String calculatedRate : calculatedRateNames) {
                logger.info("Coordinator is calculating : {}",calculatedRate);
                RateDto dto = calculator.calculateRate(calculatedRate);
                if (dto == null) {
                    continue;
                }
                rateCache.updateCalculatedRate(dto);
                kafka.produceRate(dto);
            }
        }
    }

    /**
     * Callback method when a connection to a platform is established.
     *
     * @param platformName The name of the platform
     * @param status The connection status (true if connected, false if disconnected)
     */
    @Override
    public void onConnect(String platformName, Boolean status) {
        logger.info("Connected to platform {}, status: {}", platformName, status);
        if (status) {
            for (String rateName : subscribedRateNames) {
                try {
                    this.subscriberMap.get(platformName).subscribe(platformName, rateName);
                } catch (IOException error) {
                    logger.warn(error.getMessage());
                    throw new RuntimeException(error);
                }
            }
        }
    }

    /**
     * Callback method when a connection to a platform is lost.
     *
     * @param platformName The name of the platform
     * @param status The connection status (true if connected, false if disconnected)
     */
    @Override
    public void onDisConnect(String platformName, Boolean status) {
        logger.info("Disconnected from platform {}, status: {}", platformName, status);
        if (!status) {
            for (String rateName : subscribedRateNames) {
                try {
                    this.subscriberMap.get(platformName).unSubscribe(platformName, rateName);
                } catch (Exception error) {
                    logger.warn(error.getMessage());
                    throw new RuntimeException(error);
                }
            }
        }
    }

    /**
     * Callback method when a rate is available from a platform.
     *
     * @param platformName The name of the platform
     * @param rateName The name of the rate
     * @param dto The rate data
     */
    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto dto) {
        logger.info("Rate available for platform {} -- rateName {}", platformName, rateName);
        dto.setStatus(RateStatus.AVAILABLE);
        this.rateStatusMap.put(rateName, RateStatus.AVAILABLE);
        rateCache.updateRawRate(dto);
        kafka.produceRate(dto);
    }

    /**
     * Callback method when a rate is updated from a platform.
     *
     * @param platformName The name of the platform
     * @param rateName The name of the rate
     * @param dto The rate data
     */
    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto dto) {
        logger.info("Rate updated for platform {} -- rateName {}", platformName, rateName);
        dto.setStatus(RateStatus.UPDATED);
        this.rateStatusMap.put(rateName, RateStatus.UPDATED);
        rateCache.updateRawRate(dto);
        kafka.produceRate(dto);
    }

    /**
     * Returns the current status of a rate for a given platform.
     *
     * @param platformName The name of the platform
     * @param rateName The name of the rate
     * @return The current status of the rate
     */
    @Override
    public RateStatus onRateStatus(String platformName, String rateName) {
        logger.info("Rate status for platform {} -- rateName {} -- status {}", platformName, rateName, this.rateStatusMap.get(rateName));
        return rateStatusMap.get(rateName);
    }
}
