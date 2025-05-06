package com.toyotabackend.mainplatform.Cache;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.toyotabackend.mainplatform.Dto.RateDto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service class responsible for caching rate data using Hazelcast.
 * <p>
 * Provides basic CRUD operations for rate data within a distributed map.
 */
public class HazelcastCache {


    private final HazelcastInstance hazelcastInstance;
    private final Logger cacheLogger = LogManager.getLogger("CacheLogger");
    private final IMap<String,RateDto> rawCache;
    private final IMap<String,RateDto> calculatedCache;

    /**
     * Constructs the HazelcastCacheService with the given Hazelcast instance.
     *
     * @param hazelcastInstance the Hazelcast instance to be used for caching
     */
    public HazelcastCache() {
        cacheLogger.info("Rate cache initialized");
        this.hazelcastInstance = Hazelcast.getHazelcastInstanceByName("main-hazelcast-instance");
        this.rawCache = hazelcastInstance.getMap("raw-rates");
        this.calculatedCache = hazelcastInstance.getMap("calculated-rates");
        cacheLogger.info("Rate cache has been created");
    }

    public List<RateDto> getRawRates(String rateSymbol){
        cacheLogger.info("Rates with \'{}\' symbol has been requested from raw rate cache", rateSymbol);
        List<RateDto> rawRatesSymbol = new ArrayList<>();
        Collection<RateDto> rawRateList = rawCache.values();
        for(RateDto rawRate : rawRateList){
            if(rawRate.getRateName().contains(rateSymbol)){
                rawRatesSymbol.add(rawRate);
            }
        }
        if(rawRatesSymbol.isEmpty()){
            cacheLogger.warn("There is no raw rates with the symbol \'{}\' in raw rate cache!", rateSymbol);
            return null;
        }
        cacheLogger.info("Rate with \'{}\' symbol fetched from raw rate cache!", rateSymbol);

        return rawRatesSymbol;
    }

    public RateDto getRawRateDto(String rateName){
        cacheLogger.info("{} rate requested!",rateName);
        RateDto dto = rawCache.get(rateName);
        if(dto == null){
            cacheLogger.warn("There is no rate with {} this name",rateName);
            return null;
        }

        cacheLogger.info("{} rate has been retreived from raw rate cache!",rateName);
        return dto;
    }

    public RateDto getCalculatedRateDto(String rateName){
        cacheLogger.info("{} rate requested!",rateName);
        RateDto dto = calculatedCache.get(rateName);
        if(dto == null){
            cacheLogger.warn("There is no rate with {} this name",rateName);
            return null;
        }

        cacheLogger.info("{} rate has been retreived from calculated rate cache!",rateName);
        return dto;
    }

    public void updateRawRate(RateDto dto){
        String rawRateName = dto.getRateName();
        cacheLogger.info("Raw rate {} is getting uptaded!",rawRateName);
        rawCache.put(rawRateName,dto);
        cacheLogger.info("Raw rate {} is uptaded!",rawRateName);
    }

    public void updateCalculatedRate(RateDto dto){
        if(dto == null){
            return;
        }

        String calculatedRateName = dto.getRateName();
        cacheLogger.info("Calculated rate {} is getting uptaded!",calculatedRateName);
        calculatedCache.put(calculatedRateName,dto);
        cacheLogger.info("Calculated rate {} is uptaded!",calculatedRateName);
    }

    public void closeCache(){
        cacheLogger.info("Cache is being closed!");
        this.hazelcastInstance.shutdown();
        cacheLogger.info("Cache is closed!");
    }
}
