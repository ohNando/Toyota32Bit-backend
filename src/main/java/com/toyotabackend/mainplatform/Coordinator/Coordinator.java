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

import org.codehaus.groovy.tools.shell.IO;
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
    @Value("${rate.calculatedRates}")
    private String[] calculatedRateNames;
    private String[] subscribedRateNames;
    @Value("${subscriber.names}")
    private String[] subscriberNames;
    @Value("${rate.derivedRates}")
    private String[] derivedRateNames;

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

    @Value("${client.TCP.serverAddress}")
    private String serverAddress;

    @Value("${client.TCP.port}")
    private String serverPort;

    @Value("${client.Rest_Api.baseUrl}")
    private String restBaseUrl;

    /**
     * Constructs the Coordinator with required Kafka producer and consumer.
     *
     * @param kafkaConsumer the Kafka consumer to listen to messages
     * @param kafkaProducer the Kafka producer to send rate data
     * @throws Exception in case of initialization errors
     */
    public Coordinator(ApplicationContext applicationContext){
        logger.info("Initializing coordinator");
        this.subscribedRateNames = this.rawRateNames;
        this.rateStatusMap = new HashMap<>();
        for(String rawRate : rawRateNames){
            for(String subscriberName : subscriberNames){
                rateStatusMap.put(subscriberName + "_" + rawRateNames,RateStatus.NOT_AVAILABLE);
            }
        }
        this.subscriberMap = new HashMap<>();
        this.kafka = new Kafka();
        this.database = applicationContext.getBean("postgreSQL",DatabaseService.class);
        this.rateCache = new HazelcastCache();
        this.rateService = new RateService(this.rateCache, this.database, this.rawRateNames, this.calculatedRateNames);
        this.calculator = new RateCalculatorService(this.rateService, this.rawRateNames, this.derivedRateNames);
        this.TCPLoader();
        this.RestLoader();
        this.Connector(this.username,this.password);

        logger.info("Coordinator initialized");
        this.start();
    }

    private void TCPLoader(){
        String subscriberName = "PF1";
        logger.info("Registering TCP subscriber : " + subscriberName);
        SubscriberInterface sub = LoadSubscriberClass.loadSubscriber(
            "TCPSubscriber",
            new Class<?>[]{String.class, String.class, int.class},
            new Object[]{subscriberName, this.serverAddress, this.serverPort}
        );
        
        subscriberMap.put(subscriberName,sub);
        sub.setCoordinator(this);
    }

    private void RestLoader(){
        String subscriberName = "PF2";
        logger.info("Registering REST subscriber : " + subscriberName);
        SubscriberInterface sub = LoadSubscriberClass.loadSubscriber(
            "RESTSubscriber",
            new Class<?>[]{String.class, String.class},
            new Object[]{subscriberName, this.restBaseUrl}  
        );

        subscriberMap.put(subscriberName, sub);
        sub.setCoordinator(this);
    }

    private void Connector(String username,String password){
        try{
            for(String subscriberName : subscriberNames){
                SubscriberInterface sub = this.subscriberMap.get(subscriberName);
                if(sub == null){
                    logger.warn("Subscriber {} not found!",subscriberName);
                    throw new IllegalStateException("Failed to get subscriber" + subscriberName);
                }
                sub.connect(subscriberName, username, password);
                if(!sub.getConnectionStatus()){
                    logger.warn(subscriberName + "couldn't connected");
                    subscriberMap.remove(subscriberName);
                }
            }
        }catch(IOException e){
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close(){
        logger.info("Program is closing");
        for(String subscriberName : subscriberMap.keySet()){
            SubscriberInterface sub = this.subscriberMap.get(subscriberName);
            sub.disConnect(subscriberName, username, password);
            subscriberMap.remove(subscriberName);
        }

        rateCache.closeCache();
        logger.info("Program is shut down");
    }

    @Override
    public void run(){
        logger.info("Coordinator is running");
        while(!subscriberMap.isEmpty()){
            try{
                sleep(1000);
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }

            for(String calculatedRate : calculatedRateNames){
                RateDto dto = calculator.calculateRate(calculatedRate);
                if(dto == null){
                    continue;
                }
                rateCache.updateCalculatedRate(dto);
                kafka.produceRate(dto);
                database.updateRates(kafka.consumeRate());
            }
        }
    }

    @Override
    public void onConnect(String platformName, Boolean status){
        logger.info("Connected to platform {}, status: {}",platformName,status);
        if(status){
            for(String rateName : subscribedRateNames){
                try{
                    this.subscriberMap.get(platformName).subscribe(platformName, rateName);
                }catch(IOException error){
                    logger.warn(error.getMessage());
                    throw new RuntimeException(error);
                }
            }
        }
    }

    @Override
    public void onDisConnect(String platformName, Boolean status){
        logger.info("Disconnected from platform {}, status: {}",platformName,status);
        if(!status){
            for(String rateName : subscribedRateNames){
                try{
                    this.subscriberMap.get(platformName).unSubscribe(platformName,rateName);
                }catch(Exception error){
                    logger.warn(error.getMessage());
                    throw new RuntimeException(error);
                }
            }
        }
    }

    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto dto){
        logger.info("Rate available for platform {} -- rateName {}", platformName, rateName);
        dto.setStatus(RateStatus.AVAILABLE);
        this.rateStatusMap.put(rateName,RateStatus.AVAILABLE);
        rateCache.updateRawRate(dto);
        kafka.produceRate(dto);
        database.updateRates(kafka.consumeRate());
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateDto dto){
        logger.info("Rate updated for platform {} -- rateName {}", platformName, rateName);
        dto.setStatus(RateStatus.UPDATED);
        this.rateStatusMap.put(rateName,RateStatus.UPDATED);
        rateCache.updateRawRate(dto);
        kafka.produceRate(dto);
        database.updateRates(kafka.consumeRate());
    }

    @Override
    public RateStatus onRateStatus(String platformName, String rateName){
        return rateStatusMap.get(rateName);
    }

}