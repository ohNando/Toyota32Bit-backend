package com.toyotabackend.mainplatform.Coordinator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toyotabackend.mainplatform.ClassLoader.LoadSubscriberClass;
import com.toyotabackend.mainplatform.Data_Provider.DataProvider;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.RateFields;
import com.toyotabackend.mainplatform.Entity.RateStatus;
import com.toyotabackend.mainplatform.Hazelcast.HazelcastCacheService;
import com.toyotabackend.mainplatform.Kafka.KafkaConsumer;
import com.toyotabackend.mainplatform.Kafka.KafkaProducer;
import com.toyotabackend.mainplatform.RateCallback.RateCallback;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private HazelcastCacheService cacheService;

    Logger logger = LoggerFactory.getLogger(Coordinator.class);

    @Autowired
    public Coordinator(KafkaConsumer kafkaConsumer, KafkaProducer kafkaProducer) throws Exception {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
    }

    @PostConstruct
    public void init() throws Exception {
        logger.info("Coordinator başlatılıyor, subscriber sınıfları yükleniyor...");
        LoadSubscriberClass.loadSubscriber();
        logger.info("Subscriber sınıfları başarıyla yüklendi.");
    }

    @Override
    public void onConnect(String platformName, Boolean status) throws IOException {
        logger.info("Bağlantı başarılı: {}", platformName);
    }

    @Override
    public void onDisConnect(String platformName, Boolean status) {
        logger.warn("Bağlantı kesildi: {}", platformName);
    }

    @Override
    public void onRateAvailable(String platformName, String rateName, RateDto dto) {
        try{
            kafkaProducer.send(platformName,rateName,dto);
            cacheService.cacheRate(platformName+"_"+rateName,dto);
            logger.debug("Rate verisi Kafka'ya gönderildi - Platform: {}, Rate: {}, Data: {}", platformName, rateName, dto);
        }catch (Exception e){
            logger.error("Rate verisi Kafka'ya gönderilirken hata oluştu - Platform: {}, Rate: {}", platformName, rateName, e);
        }
    }

    @Override
    public void onRateUpdate(String platformName, String rateName, RateFields rateFields) throws JsonProcessingException {
        RateDto dto = new RateDto();
        dto.setRateName(rateName);
        dto.setBid(rateFields.getBid());
        dto.setAsk(rateFields.getAsk());
        dto.setRateUpdateTime(rateFields.getTimestamp());
        kafkaProducer.send(platformName,rateName,dto);
        cacheService.cacheRate(platformName + "_" + rateName, dto);
        logger.debug("Rate güncellendi - Platform: {}, Rate: {}, Fields: {}", platformName, rateName, rateFields);
    }

    @Override
    public void onRateStatus(String platformName, String rateName, RateStatus rateStatus) {
        logger.debug("Rate durumu güncellendi - Platform: {}, Rate: {}, Status: {}", platformName, rateName, rateStatus);
    }

    private void subscriber(String platformName,String username,String password) throws IOException {
        logger.info("Platform aboneliği başlatılıyor: {}", platformName);
        for(DataProvider dataProvider : dataProviders){
            for(String rateName : rateNames){
                try{
                    dataProvider.subscribe(platformName,rateName);
                    logger.info("Abone olundu -> Platform: {}, Rate: {}", platformName, rateName);
                }catch(Exception e){
                    logger.error("Abonelik sırasında hata - Platform: {}, Rate: {}", platformName, rateName, e);
                }
            }
        }
    }

    @Override
    public void run(){
        logger.info("Coordinator thread çalışıyor.");
        try {
            subscriber("PF2","admin","12345");
            kafkaConsumer.listen("rates");
        } catch (IOException e) {
            logger.error("Thread çalışırken bir hata oluştu", e);
        }
    }
}
