package com.toyota.toyotabackend.restapi.mapper;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.entity.Rate;

public class RateMapper {

    /**
     * Converts a Rate entity object to a RateDto object.
     *
     * @param rate the Rate entity to convert
     * @return a RateDto containing the data from the Rate entity
     */
    public static RateDto rateToDto(Rate rate) {
        RateDto dto = new RateDto();
        dto.setRateName(rate.getRateName());
        dto.setBid(rate.getBid());
        dto.setAsk(rate.getAsk());
        dto.setTimestamp(rate.getRateUpdateTime());
        return dto;
    }
}
