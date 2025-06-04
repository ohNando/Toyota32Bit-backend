class RawRateCalculator{
    def calculateAverageRate(float[] bids,float[] asks) {
        if(bids == null || asks == null || bids.length == 0 || asks.length == 0){
            println "One of the input arrays is empty in calculateAverageRate!"
            return new float[0]
        }


        float bidSum = 0.0f
        for(float bid : bids){
            bidSum += bid
        }

        float askSum = 0.0f
        for(float ask : asks){
            askSum += ask
        }

        float averageBid;
        float averageAsk;

        averageBid = bidSum / bids.length;
        averageAsk = askSum /  asks.length;

        if(Float.isNaN(averageBid) || Float.isInfinite(averageBid) ||
                Float.isNaN(averageAsk) || Float.isInfinite(averageAsk)){
            println "Error in RawRateCalculator: Result bid or ask is NaN or Infinite!"
            return new float[0]
        }

        float[] rateFields = new float[2];
        rateFields[0] = averageBid;
        rateFields[1] = averageAsk;

        return rateFields;
    }
}