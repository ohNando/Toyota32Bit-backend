package com.toyotabackend.mainplatform.Mapper;

import java.time.Instant;

import com.toyotabackend.mainplatform.Dto.RateDto;

/**
 * <p>
 * This class provides methods to convert a {@link RateDto} entity to a {@link String}
 * and vice versa, allowing easy transformation of data between different layers of the application.
 * </p>
 */
public class RateMapper {
    /**
     * Converts a string representation of a rate into a {@link RateDto} object.
     * The string should follow a specific format: "rateName|bid:label:number|ask:label:number|timestamp".
     *
     * @param response the string representing the rate data
     * @return a {@link RateDto} containing the parsed data, or null if the format is incorrect
     */
    public static RateDto stringToDTO(String response) {
        //PF1_USDTRY|36.522087|36.47199|2025-05-29T14:53:01.253475626Z
        String[] parts = response.split("\\|");
        if (parts.length < 3) return null;

        RateDto dto = new RateDto();
        dto.setRateName(parts[0]);
        dto.setBid(Float.parseFloat(parts[1]));
        dto.setAsk(Float.parseFloat(parts[2]));
        dto.setRateUpdateTime(Instant.parse(parts[3]));
        return dto;
    }

    /**
     * Converts a {@link RateDto} to a string representation.
     * The string format will be: "rateName|bid:number|ask:number|timestamp".
     *
     * @param dto the {@link RateDto} to be converted
     * @return the string representation of the {@link RateDto}
     */
    public static String rateToString(RateDto dto) {
        String rateName = dto.getRateName();
        String bid = Float.toString(dto.getBid());
        String ask = Float.toString(dto.getAsk());
        String timestamp = dto.getRateUpdateTime().toString();
        return rateName + "|" + bid + "|" + ask + "|" + timestamp;
    }
}
