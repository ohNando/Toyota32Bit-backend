package com.toyotabackend.mainplatform.Mapper;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;

public class RateMapper {
    public static RateDto mapToRateDto(Rate rate) {
        return new RateDto(
                rate.getId(),
                rate.getRateName(),
                rate.getBid(),
                rate.getAsk(),
                rate.getRateUpdateTime()
        );
    }

    public static Rate mapToRate(RateDto dto) {
        return new Rate(
          dto.getId(),
          dto.getRateName(),
          dto.getBid(),
          dto.getAsk(),
          dto.getRateUpdateTime()
        );
    }
}
