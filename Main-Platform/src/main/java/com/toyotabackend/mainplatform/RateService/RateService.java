package com.toyotabackend.mainplatform.RateService;

import java.util.ArrayList;
import java.util.List;

import com.toyotabackend.mainplatform.Cache.HazelcastCache;
import com.toyotabackend.mainplatform.Dto.RateDto;

/**
 * Service class responsible for managing raw and calculated rates.
 * The class interacts with both the Hazelcast cache and the DatabaseService to retrieve rate data.
 */
public class RateService {

    private final HazelcastCache cache;

    /**
     * Constructor to initialize the RateService with necessary dependencies.
     * 
     * @param _cache Hazelcast cache for storing and retrieving rates.
     */
    public RateService(HazelcastCache _cache) {
        this.cache = _cache;
    }


    /**
     * Retrieves a list of raw rates that contain the given symbol from the cache.
     * @param symbol The symbol to search for within the raw rate names.
     * @return A list of RateDto objects for raw rates containing the given symbol.
     */
    public List<RateDto> getRawRateContains(String symbol) {
        return cache.getRawRates(symbol);
    }
}
