package com.toyota.toyotabackend.restapi.list;

import com.toyota.toyotabackend.restapi.configuration.RateConfig;
import com.toyota.toyotabackend.restapi.entity.Rate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RateList {
    private List<Rate> rates;

    public RateList() throws IOException{
        this.rates = new ArrayList<>();
        String[] rateNames = RateConfig.getRateName();
        for (String rateName : rateNames) {
            this.addRate(rateName);
            this.getRate(rateName).start();
        }
    }

    public void addRate(String rateName){
        float rateBid = RateConfig.getRateBid(rateName);
        float rateAsk = RateConfig.getRateAsk(rateName);
        rates.add( new Rate(rateName, rateBid, rateAsk) );
    }

    public Rate getRate(String rateName){
        for(Rate rate : rates){
            if(rate.getRateName().equals(rateName.trim())){
                return rate;
            }
        }
        return null;
    }

    public void removeRate(String rateName) {
        for (Rate rate : this.rates) {
            if (rate.getName().equals(rateName)) {
                rate.stopRate();
                rates.remove(rate);
            }
        }
    }
}
