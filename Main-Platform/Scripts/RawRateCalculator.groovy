class RawRateCalculator{
    def calculateAverageRate(float[] bids,float[] asks) {
        float averageBid = 0;
        for(float bid : bids){
            averageBid += bid;
        }
        averageBid /= bids.length;

        float averageAsk = 0;
        for(float ask : asks){
            averageAsk += ask;
        }
        averageAsk /= asks.length;

        float[] rateFields = new double[2];
        rateFields[0] = averageBid;
        rateFields[1] = averageAsk;

        return rateFields;
    }
}