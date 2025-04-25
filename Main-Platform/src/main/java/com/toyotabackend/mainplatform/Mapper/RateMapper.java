package com.toyotabackend.mainplatform.Mapper;

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
        return new RateDto(
                rate.getRateName(),
                rate.getBid(),
                rate.getAsk(),
                rate.getRateUpdateTime()
        );
    }

    /**
     * Converts a {@link RateDto} data transfer object to a {@link Rate} entity.
     *
     * @param dto the {@link RateDto} to be converted
     * @return a {@link Rate} entity containing the same data as the {@link RateDto}
     */
    public static Rate mapToRate(RateDto dto) {
        return new Rate(
                dto.getRateName(),
                dto.getBid(),
                dto.getAsk(),
                dto.getRateUpdateTime()
        );
    }
}
