package com.toyota.toyotabackend.restapi.producer;

import com.toyota.toyotabackend.restapi.configuration.RateConfig;
import org.springframework.stereotype.Component;

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
    /**
     * Generates a bid and ask rate for the given currency by using the base rate and applying random variations.
     *
     * @param rateName the currency for which the bid and ask rates are to be generated.
     * @return a {@link Map} containing the bid and ask rates for the given currency.
     * @throws IllegalArgumentException if the base rate for the given currency is not found.
     */
    public static float[] generateRates(String rateName) {
        Random random = new Random();
        boolean incOrDec;
        float randRatePerc;
        float[] rateParts = new float[2];
        rateParts[0] = RateConfig.getRateBid(rateName);
        rateParts[1] = RateConfig.getRateBid(rateName);

        float minPerc = 0.001f;
        float maxPerc = 0.005f;

        randRatePerc = minPerc + random.nextFloat() * (maxPerc - minPerc);

        incOrDec = random.nextBoolean();
        if (incOrDec) {
            rateParts[0] += rateParts[0] * randRatePerc;
            rateParts[1] += rateParts[1] * randRatePerc;
        }else{
            rateParts[0] -= rateParts[0] * randRatePerc;
            rateParts[1] -= rateParts[1] * randRatePerc;
        }
        return rateParts;
    }
}
