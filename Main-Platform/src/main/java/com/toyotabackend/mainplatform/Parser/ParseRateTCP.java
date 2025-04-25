package com.toyotabackend.mainplatform.Parser;

import com.toyotabackend.mainplatform.Dto.RateDto;

/**
 * A utility class for parsing rate data from TCP response strings.
 * <p>
 * This class is responsible for parsing a raw TCP response string into a {@link RateDto} object.
 * The response string is expected to contain rate information, which is extracted and transformed
 * into a structured {@link RateDto}.
 */
public class ParseRateTCP {

    /**
     * Parses a TCP response string into a {@link RateDto} object.
     * <p>
     * The response string is expected to be in the following format:
     * <pre>
     * rateName|bid:xxx|ask:xxx|timestamp:xxx:xxx
     * </pre>
     * The method splits the response into parts and extracts the rate information, converting it into
     * a {@link RateDto} object. If the response format is incorrect, an error message is printed,
     * and the method returns null.
     *
     * @param response the raw TCP response string to be parsed
     * @return a {@link RateDto} object containing the parsed rate data, or null if parsing fails
     */
    public RateDto parseRate(String response) {
        try {
            // Split the response by '|'
            String[] parts = response.split("\\|");
            if (parts.length < 4) {
                throw new IllegalArgumentException("!)|Cannot parse rate : " + response);
            }

            // Extract rate name
            String rateName = parts[0];

            // Parse bid and ask values
            String[] bids = parts[1].split(":");
            float bid = Float.parseFloat(bids[2]);

            String[] asks = parts[2].split(":");
            float ask = Float.parseFloat(asks[2]);

            // Parse timestamp
            String[] timeParts = parts[3].split(":");
            StringBuilder timestampBuilder = new StringBuilder();
            for (int i = 2; i < timeParts.length; i++) {
                if (i > 2) timestampBuilder.append(":");
                timestampBuilder.append(timeParts[i]);
            }
            String rateUpdateTime = timestampBuilder.toString().replace("Z", "");

            // Return the parsed RateDto
            return new RateDto(rateName, bid, ask, rateUpdateTime);
        } catch (Exception e) {
            // Print error message if parsing fails
            System.out.println("(!)|Cannot parse rate : " + e.getMessage());
            return null;
        }
    }
}
