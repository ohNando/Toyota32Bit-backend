package com.toyotabackend.mainplatform.RateService;

import java.util.ArrayList;
import java.util.List;

import com.toyotabackend.mainplatform.Cache.HazelcastCache;
import com.toyotabackend.mainplatform.Dto.RateDto;

public class RateService {
    private final HazelcastCache cache;
    private final DatabaseService database;

    private String[] rawRates;
    private String[] calculatedRates;

    public RateService(HazelcastCache _cache,DatabaseService _database,
                        String[] _rawRates,String[] _calculatedRates
                        ){
        this.cache = _cache;
        this.database = _database;
        this.rawRates = _rawRates;
        this.calculatedRates = _calculatedRates;
    }

    public RateDto getRawRate(String rateName){
        RateDto dto = cache.getRawRateDto(rateName);
        if(dto == null){
            return database.getLastestRate(rateName);
        }
        return dto;
    }

    public RateDto getCalculatedRate(String rateName){
        RateDto dto = cache.getCalculatedRateDto(rateName);
        if(dto == null){
            dto = database.getLastestRate(rateName);
            if(dto != null){
                return dto;
            }
            return null;
        }
        return dto;
    }

    public List<RateDto> getRawRateContains(String symbol){
        List<RateDto> dtoList = cache.getRawRates(symbol);
        if(dtoList == null){
            dtoList = new ArrayList<>();
            for(String rawRate : rawRates){
                RateDto dto = database.getLastestRate(rawRate);
                if(dto != null && dto.getRateName().contains(symbol)){
                    dtoList.add(dto);
                }
            }
            return dtoList;
        }
        return dtoList;
    }
}
