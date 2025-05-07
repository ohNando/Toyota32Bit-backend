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
    private final DatabaseService database;

    private String[] rawRates;
    private String[] calculatedRates;

    /**
     * Constructor to initialize the RateService with necessary dependencies.
     * 
     * @param _cache Hazelcast cache for storing and retrieving rates.
     * @param _database DatabaseService for fetching rates from the database.
     * @param _rawRates Array of raw rate names.
     * @param _calculatedRates Array of calculated rate names.
     */
    public RateService(HazelcastCache _cache, DatabaseService _database,
                        String[] _rawRates, String[] _calculatedRates) {
        this.cache = _cache;
        this.database = _database;
        this.rawRates = _rawRates;
        this.calculatedRates = _calculatedRates;
    }

    /**
     * Retrieves the raw rate from the cache if available, otherwise fetches it from the database.
     * 
     * @param rateName The name of the raw rate to be fetched.
     * @return The RateDto for the requested raw rate, or null if not found.
     */
    public RateDto getRawRate(String rateName) {
        RateDto dto = cache.getRawRateDto(rateName);
        if (dto == null) {
            return database.getLastestRate(rateName);
        }
        return dto;
    }

    /**
     * Retrieves the calculated rate from the cache if available, otherwise fetches it from the database.
     * 
     * @param rateName The name of the calculated rate to be fetched.
     * @return The RateDto for the requested calculated rate, or null if not found.
     */
    public RateDto getCalculatedRate(String rateName) {
        RateDto dto = cache.getCalculatedRateDto(rateName);
        if (dto == null) {
            dto = database.getLastestRate(rateName);
            if (dto != null) {
                return dto;
            }
            return null;
        }
        return dto;
    }

    /**
     * Retrieves a list of raw rates that contain the given symbol from the cache.
     * If not found in the cache, the method fetches the rates from the database.
     * 
     * @param symbol The symbol to search for within the raw rate names.
     * @return A list of RateDto objects for raw rates containing the given symbol.
     */
    public List<RateDto> getRawRateContains(String symbol) {
        List<RateDto> dtoList = cache.getRawRates(symbol);
        if (dtoList == null) {
            dtoList = new ArrayList<>();
            for (String rawRate : rawRates) {
                RateDto dto = database.getLastestRate(rawRate);
                if (dto != null && dto.getRateName().contains(symbol)) {
                    dtoList.add(dto);
                }
            }
            return dtoList;
        }
        return dtoList;
    }
}
