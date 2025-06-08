package Scripts

class DerivedRateCalculator{
    def calculateDerivedAverageRate(float[] RateBid1,float[] RateAsk1,float[] RateBid2,float[] RateAsk2){
        if(RateBid1 == null || RateAsk1 == null ||
            RateBid2 == null || RateAsk2 == null ||
            RateBid1.length == 0 || RateAsk1.length == 0 ||
            RateBid2.length == 0 || RateAsk2.length == 0){
            println "One of the input arrays is empty in calculateDerivedAverageRate!"
            return new float[0]
        }

        float bidSum = 0.0f
        for(float bid1 : RateBid1){
            bidSum += bid1
        }
        bidSum /= RateBid1.length

        float askSum = 0.0f
        for(float ask1 : RateAsk1){
            askSum += ask1
        }
        askSum /= RateAsk1.length

        float mid = (bidSum + askSum) / 2.0f

        float derivedBidSum = 0.0f;
        for(float bid2 : RateBid2){
            derivedBidSum += bid2
        }
        float averageRateBid2 = derivedBidSum / RateBid2.length
        float finalDerivedBid = mid * averageRateBid2


        float derivedAskSum = 0.0f
        for(float ask2 : RateAsk2){
            derivedAskSum += ask2
        }
        float averageRateAsk2 = derivedAskSum / RateAsk2.length
        float finalDerivedAsk = mid * averageRateAsk2

        float[] derivedRateFields = new float[2]
        derivedRateFields[0] = finalDerivedBid
        derivedRateFields[1] = finalDerivedAsk

        if(Float.isNaN(finalDerivedBid) || Float.isInfinite(finalDerivedBid) ||
           Float.isNaN(finalDerivedAsk) || Float.isInfinite(finalDerivedAsk)){
            println "Error in DerivedRateCalculator: Result bid or ask is NaN or Infinite!"
            return new float[0]
        }

        return derivedRateFields
    }
}