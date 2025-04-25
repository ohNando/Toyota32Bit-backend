package com.toyotabackend.mainplatform.Hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.toyotabackend.mainplatform.Dto.RateDto;
import org.springframework.stereotype.Service;

@Service
public class HazelcastCacheService {
    private final HazelcastInstance hazelcastInstance;

    public HazelcastCacheService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void cacheRate(String key, RateDto rateDto) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        map.put(key, rateDto);
    }

    public void updateRate(String key, RateDto rateDto) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        RateDto currentRate = map.get(key);
        if (currentRate != null) {
            currentRate.setBid(rateDto.getBid());
            currentRate.setAsk(rateDto.getAsk());
            map.put(key, currentRate);
        }
    }

    public RateDto getRate(String key) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        return map.get(key);
    }

    public void removeRate(String key) {
        IMap<String, RateDto> map = hazelcastInstance.getMap("rates");
        map.remove(key);
    }
}
