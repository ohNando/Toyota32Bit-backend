package com.toyotabackend.mainplatform.RateCalculator;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.RateService.RateService;

public class RateCalculatorService {
    private GroovyClassLoader groovyClassLoader;
    private RateService service;
    private String[] rawRateNames;
    private String[] derivedRateNames;
    private final Logger logger = LogManager.getLogger("Calculator");

    private final String rawRateCalculatorScriptPath = System.getProperty("user.dir") + "/Main-Platform/Scripts/RawRateCalculator.groovy";
    private final String derivedRateCalculaterScriptPath = System.getProperty("user.dir") + "/Main-Platform/Scripts/DerivedRateCalculator.groovy";


    public RateCalculatorService(RateService _service,String[] rawRates,String[] derivedRates) {
        this.service = _service;
        this.rawRateNames = rawRates;
        this.derivedRateNames = derivedRates;
    }

    public float[] calculateRawRatesWithMethod(float[] bids,float[] asks) {
        groovyClassLoader = new GroovyClassLoader();
        try{
            File groovyFile = new File(rawRateCalculatorScriptPath);
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyFile);
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculateAverageRate", float[].class,float[].class);
            return (float[]) method.invoke(groovyObject,bids,asks);
        }catch(Exception e){
            logger.warn(e.getMessage());
        }
        return null;
    }

    public float[] calculateDerivedRatesWithMethod(float[] bidRates1,float[] askRates1,
                                                float[] bidRates2,float[] askRates2){
        groovyClassLoader = new GroovyClassLoader();
                            
        try{
            File groovyFile = new File(derivedRateCalculaterScriptPath);
            Class<?> groovyClass = groovyClassLoader.parseClass(groovyFile);
            Object groovyObject = groovyClass.newInstance();

            Method method = groovyClass.getMethod("calculateDerivedAverageRate", float[].class,float[].class,float[].class,float[].class);
            return (float[]) method.invoke(groovyObject,bidRates1,askRates1,bidRates2,askRates2);
        }catch(Exception e){
            logger.warn(e.getMessage());
        }
        return null;
    }

    public RateDto calculateRate(String rateName){
        for(String rawRate : rawRateNames){
            if(rawRate.equals(rateName)){
                return calculateRawRate(rateName);
            }
        }
        for(String derivedRate : derivedRateNames){
            if(derivedRate.equals(rateName)){
                return calculateDerivedRate(rateName);
            }
        }
        return null;
    }

    public RateDto calculateRawRate(String rateName){
        logger.info("Calculating raw rate : {}",rateName);
        List<RateDto> rawRateList = service.getRawRateContains(rateName);
        if(rawRateList.isEmpty()){
            return null;
        }

        float[] bids = new float[rawRateList.size()];
        float[] asks = new float[rawRateList.size()];
        
        for(int i = 0; i < rawRateList.size(); i++){
            bids[i] = rawRateList.get(i).getBid();
        }
        for(int i = 0; i < rawRateList.size(); i++){
            asks[i] = rawRateList.get(i).getAsk();
        }
        float[] rateFields = (float[]) calculateRawRatesWithMethod(bids, asks);
        logger.info("Raw Rate calculated : {}",rateName);

        return new RateDto(rateName,rateFields[0],rateFields[1],rawRateList.get(0).getTimestamp());
    }

    //USDTRY -> USD , TRY
    public RateDto calculateDerivedRate(String rateName){
        logger.info("Calculating derived rate : {}",rateName);
        List<RateDto> rawRateList1 = service.getRawRateContains(rateName.substring(0,3));
        List<RateDto> rawRateList2 = service.getRawRateContains(rateName.substring(3,6));

        if(rawRateList1.isEmpty() || rawRateList2.isEmpty()){
            logger.error("Raw rates are missing for calculation : {}",rateName);
            return null;
        }

        float[] rateBids1 = new float[rawRateList1.size()];
        float[] rateAsks1 = new float[rawRateList1.size()];
        float[] rateBids2 = new float[rawRateList2.size()];
        float[] rateAsks2 = new float[rawRateList2.size()];

        for(int i=0 ; i<rawRateList1.size();i++){
            rateBids1[i] = rawRateList1.get(i).getBid();
        }
        for(int i=0 ; i<rawRateList2.size();i++){
            rateBids2[i] = rawRateList2.get(i).getBid();
        }

        for(int i=0;i<rawRateList1.size();i++){
            rateAsks1[i] = rawRateList1.get(i).getAsk();
        }
        for(int i=0;i<rawRateList2.size();i++){
            rateAsks2[i] = rawRateList2.get(i).getAsk();
        }

        float[] rateFields = (float[]) calculateDerivedRatesWithMethod(rateBids1, rateAsks1, rateBids2, rateAsks2);
        if(rateFields == null){
            logger.warn("Calculator script returned null");
            throw new IllegalStateException("Calculator script returned null");
        }

        logger.info("Derived rate calculated : {}",rateName);
        return new RateDto(rateName,rateFields[0],rateFields[1],rawRateList2.get(0).getTimestamp());
    }
}
