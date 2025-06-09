package com.toyota.toyotabackend.restapi.list;

import com.toyota.toyotabackend.restapi.configuration.RateConfig;
import com.toyota.toyotabackend.restapi.entity.Rate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of Rate objects, initializing them based on configuration,
 * and providing methods to add, retrieve, and remove rates.
 * <p>
 * This component loads rates from the configured properties file and
 * starts each Rate thread to simulate live rate updates.
 */
@Component
public class RateList {
    private List<Rate> rates;

    /**
     * Constructs the RateList by loading all configured rate names from RateConfig,
     * adding each Rate to the list, and starting their update threads.
     *
     * @throws IOException if rate configuration cannot be loaded
     */
    public RateList() throws IOException{
        this.rates = new ArrayList<>();
        String[] rateNames = RateConfig.getRateName();
        for (String rateName : rateNames) {
            this.addRate(rateName);
            this.getRate(rateName).start();
        }
    }

    /**
     * Adds a new Rate object to the list based on the given rate name.
     * The bid and ask values are retrieved from the RateConfig.
     *
     * @param rateName the name of the rate to add
     */
    public void addRate(String rateName){
        float rateBid = RateConfig.getRateBid(rateName);
        float rateAsk = RateConfig.getRateAsk(rateName);
        rates.add( new Rate(rateName, rateBid, rateAsk) );
    }

    /**
     * Retrieves the Rate object corresponding to the given rate name.
     *
     * @param rateName the name of the rate to retrieve
     * @return the Rate object if found, otherwise null
     */
    public Rate getRate(String rateName){
        for(Rate rate : rates){
            if(rate.getRateName().equals(rateName.trim())){
                return rate;
            }
        }
        return null;
    }
}
