package com.toyotabackend.mainplatform.Hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.toyotabackend.mainplatform.Dto.RateDto;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for caching rate data using Hazelcast.
 * <p>
 * Provides basic CRUD operations for rate data within a distributed map.
 */
@Service
public class HazelcastCacheService {

    private final HazelcastInstance hazelcastInstance;

    /**
     * Constructs the HazelcastCacheService with the given Hazelcast instance.
     *
     * @param hazelcastInstance the Hazelcast instance to be used for caching
     */
    public HazelcastCacheService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * Caches the given rate data under the specified key.
     *
     * @param key      the cache key
     * @param rateDto  the rate data to be cached
     */
    public void cacheRate(String key, RateDto rateDto) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        map.put(key, rateDto);
    }

    /**
     * Updates the existing rate data in the cache if it exists.
     * Only bid and ask values are updated.
     *
     * @param key      the cache key
     * @param rateDto  the new rate data
     */
    public void updateRate(String key, RateDto rateDto) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        RateDto currentRate = map.get(key);
        if (currentRate != null) {
            currentRate.setBid(rateDto.getBid());
            currentRate.setAsk(rateDto.getAsk());
            map.put(key, currentRate);
        }
    }

    /**
     * Retrieves rate data from the cache for the given key.
     *
     * @param key the cache key
     * @return the cached RateDto or null if not found
     */
    public RateDto getRate(String key) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        return map.get(key);
    }

    /**
     * Removes the rate data from the cache for the specified key.
     *
     * @param key the cache key
     */
    public void removeRate(String key) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        map.remove(key);
    }
}
