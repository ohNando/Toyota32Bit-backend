package com.toyotabackend.mainplatform.Coordinator;

import com.toyotabackend.mainplatform.ClassLoader.LoadSubscriberClass;
import com.toyotabackend.mainplatform.Data_Provider.DataProvider;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.RateFields;
import com.toyotabackend.mainplatform.Entity.RateStatus;
import com.toyotabackend.mainplatform.Kafka.KafkaConsumer;
import com.toyotabackend.mainplatform.Kafka.KafkaProducer;
import com.toyotabackend.mainplatform.RateCallback.RateCallback;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Coordinator extends Thread implements RateCallback {
    @Value("${rate.server.tcp.jarPath}")
    private String tcpJarPath;
    @Value("${rate.server.tcp.mainPath}")
    private String tcpMainPath;
    @Value("${rate.server.rest.jarPath}")
    private String restJarPath;
    @Value("${rate.server.rest.mainPath}")
    private String restMainPath;

    @Value("${rate.names}")
    private List<String> rateNames;


    @Lazy
    @Autowired
    private List<DataProvider> dataProviders;
    private KafkaConsumer kafkaConsumer;
    private KafkaProducer kafkaProducer;

    @Autowired
    public Coordinator(KafkaConsumer kafkaConsumer, KafkaProducer kafkaProducer) throws Exception {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
    }

    @PostConstruct
    public void init() throws Exception {
        LoadSubscriberClass.loadSubscriber();
    }

    @Override
    public void onConnect(String platformName, Boolean status) throws IOException {
        System.out.println("Connected to " + platformName);

    }

    @Override
    public void onDisConnect(String platformName, Boolean status) {

    }

    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto dto) {
        try{
            kafkaProducer.send(platformName,rateName,dto);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateFields rateFields) {

    }

    @Override
    public void onRateStatus(String platformName, String rateName, RateStatus rateStatus) {

    }

    private void subscriber(String platformName,String username,String password) throws IOException {
        for(DataProvider dataProvider : dataProviders){
            for(String rateName : rateNames){
                dataProvider.subscribe(platformName,rateName);
            }
        }
    }

    @Override
    public void run(){
        try {
            subscriber("PF2","admin","12345");
            kafkaConsumer.listen("rates");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
