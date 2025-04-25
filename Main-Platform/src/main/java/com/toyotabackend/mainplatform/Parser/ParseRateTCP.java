package com.toyotabackend.mainplatform.Parser;

import com.toyotabackend.mainplatform.Dto.RateDto;

public class ParseRateTCP {
    public RateDto parseRate(String response) {
        try{
            String[] parts = response.split("\\|");
            if(parts.length < 4){
                throw new IllegalArgumentException("!)|Cannot parse rate : " + response);
            }
            String rateName = parts[0];
            String[] bids = parts[1].split(":");
            float bid = Float.parseFloat(bids[2]);

            String[] asks = parts[2].split(":");
            float ask = Float.parseFloat(asks[2]);

            String[] timeParts = parts[3].split(":");
            StringBuilder timestampBuilder = new StringBuilder();
            for (int i = 2; i < timeParts.length; i++) {
                if (i > 2) timestampBuilder.append(":");
                timestampBuilder.append(timeParts[i]);
            }
            String rateUpdateTime = timestampBuilder.toString().replace("Z", "");

            return new RateDto(rateName, bid, ask, rateUpdateTime);
        }catch(Exception e){
            System.out.println("(!)|Cannot parse rate : " + e.getMessage());
            return null;
        }
    }
}
