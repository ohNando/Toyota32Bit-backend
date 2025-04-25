package com.toyota.toyotabackend.restapi.producer;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is responsible for generating and updating bid-ask currency rates based on a predefined base rate.
 * <p>
 * The generated bid and ask rates are based on the base rate, with a random variation to simulate real market data.
 * </p>
 */
@Component
public class RateProducer {
    private final Map<String, Double> initialRates = new HashMap<>();
    private final Random random = new Random();

    /**
     * Constructor that initializes the {@link RateProducer} by loading the allowed rates and their corresponding
     * base values from the environment properties.
     *
     * @param environment the Spring environment used to retrieve the configuration properties.
     */
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

    /**
     * Generates a bid and ask rate for the given currency by using the base rate and applying random variations.
     *
     * @param currency the currency for which the bid and ask rates are to be generated.
     * @return a {@link Map} containing the bid and ask rates for the given currency.
     * @throws IllegalArgumentException if the base rate for the given currency is not found.
     */
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

    /**
     * Updates the base rate by applying a random fluctuation between 5% and 15% to simulate market changes.
     *
     * @param baseRate the base rate to be updated.
     * @return the updated rate with the random fluctuation applied.
     */
    private Double updateRate(Double baseRate) {
        return baseRate + (baseRate * random.nextDouble() * 0.1 + 0.05);
    }
}
