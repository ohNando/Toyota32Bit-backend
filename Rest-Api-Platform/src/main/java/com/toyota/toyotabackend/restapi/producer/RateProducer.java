package com.toyota.toyotabackend.restapi.producer;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class RateProducer {
    private final Map<String, Double> initialRates = new HashMap<>();
    private final Random random = new Random();

    public RateProducer(Environment environment) {
        String[] allowedRates = environment.getProperty("rates.allowed-rates", "").split(",");

        for (String currency : allowedRates) {
            String key = "rates.base." + currency.trim();
            String value = environment.getProperty(key);

            if (value != null) {
                initialRates.put(currency.trim(), Double.parseDouble(value));
            }
        }
    }

    public Map<String, Double> generateRates(String currency) {
        Double baseRate = initialRates.get(currency);

        if (baseRate == null) {
            throw new IllegalArgumentException("Rate not found for: " + currency);
        }

        Double bid = updateRate(baseRate);
        Double ask = bid + (bid * 0.025);

        Map<String, Double> rates = new HashMap<>();
        rates.put("bid", bid);
        rates.put("ask", ask);

        return rates;
    }

    private Double updateRate(Double baseRate) {
        return baseRate + (baseRate * random.nextDouble() * 0.1 + 0.05);
    }
}
