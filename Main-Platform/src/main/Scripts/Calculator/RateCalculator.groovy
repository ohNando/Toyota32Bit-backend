class RateCalculator{
    static Map<String, Float> calculateAverageRate(float bid1, float ask1, float bid2, float ask2) {
        float averageBid = (bid1 + bid2) / 2
        float averageAsk = (ask1 + ask2) / 2

        return [
                averageBid: averageBid,
                averageAsk: averageAsk
        ]
    }
}