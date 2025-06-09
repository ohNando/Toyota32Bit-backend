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

    /**
     * Checks whether the given RateDto's bid and ask values are within
     * a 1% tolerance compared to the last cached rate.
     * <p>
     * If no previous rate exists in cache, it returns true (accept the rate).
     * </p>
     *
     * @param dto The new RateDto to validate.
     * @return true if bid and ask changes are both within ±1%, false otherwise.
     */
    public boolean isWithinTolerance(RateDto dto) {
        RateDto lastDto = cache.getRawRateDto(dto.getRateName());

        //If the cache has no rate it should accept
        if(lastDto == null) { return true; }

        //Getting rate parts
        float newBid = dto.getBid();
        float newAsk = dto.getAsk();
        float oldBid = lastDto.getBid();
        float oldAsk = lastDto.getAsk();

        float bidChange = Math.abs((newBid - oldBid) / oldBid);
        float askChange = Math.abs((newAsk - oldAsk) / oldAsk);

        //If the change is less than %1 then we accept the rate
        return bidChange <= 0.01f && askChange <= 0.01f;
    }
}
