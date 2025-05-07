package com.toyotabackend.mainplatform.Mapper;

import java.time.Instant;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.Rate;

/**
 * Utility class for mapping between {@link Rate} entity and {@link RateDto} data transfer object.
 * <p>
 * This class provides methods to convert a {@link Rate} entity to a {@link RateDto}
 * and vice versa, allowing easy transformation of data between different layers of the application.
 * It also provides methods to map rate data to/from strings for easier communication.
 * </p>
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

    /**
     * Converts a string representation of a rate into a {@link RateDto} object.
     * The string should follow a specific format: "rateName|bid:label:number|ask:label:number|timestamp".
     *
     * @param response the string representing the rate data
     * @return a {@link RateDto} containing the parsed data, or null if the format is incorrect
     */
    public static RateDto stringToDTO(String response) {
        String[] parts = response.split("\\|");
        if (parts.length < 3) return null;

        RateDto dto = new RateDto();
        dto.setRateName(parts[0]);

        String[] bidParts = parts[1].split(":");
        if (bidParts.length == 3 && bidParts[1].equals("number")) {
            dto.setBid(Float.parseFloat(bidParts[2]));
        }

        String[] askParts = parts[2].split(":");
        if (askParts.length == 3 && askParts[1].equals("number")) {
            dto.setAsk(Float.parseFloat(askParts[2]));
        }
        
        Instant timestamp = Instant.parse(parts[3]);
        dto.setTimestamp(timestamp);
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
        String timestamp = dto.getTimestamp().toString();
        String key = rateName + "|" + bid + "|" + ask + "|" + timestamp;
        return key;
    }
}
