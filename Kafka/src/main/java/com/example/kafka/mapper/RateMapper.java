package com.example.kafka.mapper;

import com.example.kafka.entity.Rate;

import java.time.Instant;

/**
 * Utility class responsible for mapping data from raw String format to Rate entity objects.
 */
public class RateMapper {
    /**
     * Converts a string representation of a rate into a {@link Rate} object.
     * <p>
     * The expected input format is: <code>"rateName|bid|ask|rateUpdateTime"</code>,
     * where:
     * <ul>
     *     <li><code>rateName</code> is a string representing the name of the rate (e.g., "USD/EUR")</li>
     *     <li><code>bid</code> and <code>ask</code> are float values</li>
     *     <li><code>rateUpdateTime</code> is an ISO-8601 formatted timestamp (e.g., "2024-06-09T14:23:00Z")</li>
     * </ul>
     * </p>
     *
     * @param stringRate the raw string representing the rate
     * @return a {@link Rate} object populated with the parsed data
     * @throws ArrayIndexOutOfBoundsException or {@link NumberFormatException}
     *         if the input format is invalid or contains non-numeric data
     */
    public static Rate stringToRate(String stringRate) {
        Rate rate = new Rate();
        String[] rateFields = stringRate.split("\\|");

        rate.setRateName(rateFields[0]);
        rate.setBid(Float.parseFloat(rateFields[1]));
        rate.setAsk(Float.parseFloat(rateFields[2]));
        rate.setRateUpdateTime(Instant.parse(rateFields[3]));
        return rate;
    }
}
