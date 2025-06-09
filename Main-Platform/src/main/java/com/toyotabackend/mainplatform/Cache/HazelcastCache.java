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
 * Manages two separate caches: raw rates and calculated rates.
 */
public class HazelcastCache {

    private final HazelcastInstance hazelcastInstance;
    private final Logger cacheLogger = LogManager.getLogger(HazelcastCache.class);
    private final IMap<String, RateDto> rawCache;
    private final IMap<String, RateDto> calculatedCache;

    /**
     * Initializes the Hazelcast instance and retrieves distributed maps
     * for raw and calculated rate data.
     */
    public HazelcastCache() {
        cacheLogger.info("Rate cache initialized");
        this.hazelcastInstance = Hazelcast.getHazelcastInstanceByName("main-hazelcast-instance");
        this.rawCache = hazelcastInstance.getMap("raw-rates");
        this.calculatedCache = hazelcastInstance.getMap("calculated-rates");
        cacheLogger.info("Rate cache has been created");
    }

    /**
     * Retrieves a list of raw rate entries matching the given symbol.
     *
     * @param rateSymbol the symbol to search for
     * @return list of matching RateDto objects or null if not found
     */
    public List<RateDto> getRawRates(String rateSymbol) {
        cacheLogger.info("Rates with '{}' symbol requested from raw rate cache", rateSymbol);
        List<RateDto> rawRatesSymbol = new ArrayList<>();
        Collection<RateDto> rawRateList = rawCache.values();
        for (RateDto rawRate : rawRateList) {
            if (rawRate.getRateName().contains(rateSymbol)) {
                rawRatesSymbol.add(rawRate);
            }
        }
        if (rawRatesSymbol.isEmpty()) {
            cacheLogger.warn("No raw rates found with symbol '{}'", rateSymbol);
            return null;
        }
        cacheLogger.info("Rates with '{}' symbol fetched successfully", rateSymbol);
        return rawRatesSymbol;
    }

    /**
     * Retrieves a raw rate by its name.
     *
     * @param rateName the name of the rate
     * @return the corresponding RateDto or null if not found
     */
    public RateDto getRawRateDto(String rateName) {
        cacheLogger.info("{} rate requested", rateName);
        RateDto dto = rawCache.get(rateName);
        if (dto == null) {
            cacheLogger.warn("No raw rate found with name {}", rateName);
            return null;
        }
        cacheLogger.info("{} raw rate retrieved successfully", rateName);
        return dto;
    }

    /**
     * Retrieves a calculated rate by its name.
     *
     * @param rateName the name of the rate
     * @return the corresponding RateDto or null if not found
     */
    public RateDto getCalculatedRateDto(String rateName) {
        cacheLogger.info("{} rate requested", rateName);
        RateDto dto = calculatedCache.get(rateName);
        if (dto == null) {
            cacheLogger.warn("No calculated rate found with name {}", rateName);
            return null;
        }
        cacheLogger.info("{} calculated rate retrieved successfully", rateName);
        return dto;
    }

    /**
     * Updates or inserts a raw rate in the cache.
     *
     * @param dto the RateDto to update
     */
    public void updateRawRate(RateDto dto) {
        String rawRateName = dto.getRateName();
        cacheLogger.info("Updating raw rate: {}", rawRateName);
        rawCache.put(rawRateName, dto);
        cacheLogger.info("Raw rate {} updated", rawRateName);
    }

    /**
     * Updates or inserts a calculated rate in the cache.
     *
     * @param dto the RateDto to update
     */
    public void updateCalculatedRate(RateDto dto) {
        if (dto == null) return;
        String calculatedRateName = dto.getRateName();
        cacheLogger.info("Updating calculated rate: {}", calculatedRateName);
        calculatedCache.put(calculatedRateName, dto);
        cacheLogger.info("Calculated rate {} updated", calculatedRateName);
    }

    /**
     * Shuts down the Hazelcast instance and clears cache resources.
     */
    public void closeCache() {
        cacheLogger.info("Closing cache...");
        this.hazelcastInstance.shutdown();
        cacheLogger.info("Cache closed successfully");
    }
}
