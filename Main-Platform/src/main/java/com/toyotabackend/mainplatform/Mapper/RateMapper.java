package com.toyotabackend.mainplatform.Mapper;

import java.time.Instant;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;

/**
 * Utility class for mapping between {@link Rate} entity and {@link RateDto} data transfer object.
 * <p>
 * This class provides methods to convert a {@link Rate} entity to a {@link RateDto}
 * and vice versa, allowing easy transformation of data between different layers of the application.
 */
public class RateMapper {

    /**
     * Converts a {@link Rate} entity to a {@link RateDto} data transfer object.
     *
     * @param rate the {@link Rate} entity to be converted
     * @return a {@link RateDto} containing the same data as the {@link Rate} entity
     */
    public static RateDto mapToRateDto(Rate rate) {
        RateDto dto = new RateDto();
        dto.setRateName(rate.rateName);
        dto.setAsk(rate.ask);
        dto.setBid(rate.bid);
        dto.setTimestamp(rate.rateUpdateTime);

        return dto;
    }

    /**
     * Converts a {@link RateDto} data transfer object to a {@link Rate} entity.
     *
     * @param dto the {@link RateDto} to be converted
     * @return a {@link Rate} entity containing the same data as the {@link RateDto}
     */
    public static Rate mapToRate(RateDto dto) {
        Rate rate = new Rate();
        rate.rateName = dto.getRateName();
        rate.bid = dto.getBid();
        rate.ask = dto.getAsk();
        rate.rateUpdateTime = dto.getTimestamp();

        return rate;
    }

    public static RateDto stringToDTO(String response){
        String[] parts = response.split("\\|");
        String rateName = parts[0];
        float bid = Float.parseFloat(parts[1]);
        float ask = Float.parseFloat(parts[2]);
        Instant timestamp = Instant.parse(parts[3]);
        return new RateDto(rateName,bid,ask,timestamp);
    }

    public static String rateToString(RateDto dto){
        String rateName = dto.getRateName();
        String bid = Float.toString(dto.getBid());
        String ask = Float.toString(dto.getAsk());
        String timestamp = dto.getTimestamp().toString();
        String key = rateName + "|" + bid + "|" + ask + "|" + timestamp;
        return key;
    }
}
