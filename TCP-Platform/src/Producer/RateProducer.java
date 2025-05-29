package Producer;

import Config.RateConfig;

import java.util.Random;

/**
 * A func that generates currency rate data for a specific currency pair.
 * It produces a bid and ask rate at regular intervals based on a base rate defined
 * in the properties and sends the data to the provided output stream.
 */
public class RateProducer  {
    /**
     * Generates a bid and ask rate based on the base rate and random spread values.
     * The generated rates are formatted with the currency pair and a timestamp.
     *
     * @return A formatted string containing the bid, ask, and timestamp.
     */
    public static float[] generateRate(String rateName) {
        Random rand = new Random();
        Boolean incOrDec;
        float randRatePerc;
        float[] rateParts = new float[2];
        rateParts[0] = RateConfig.getRateBid(rateName);
        rateParts[1] = RateConfig.getRateAsk(rateName);

        float minPerc = 0.001f;
        float maxPerc = 0.005f;

        randRatePerc = minPerc + rand.nextFloat() * (maxPerc - minPerc);

        incOrDec = rand.nextBoolean();
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
