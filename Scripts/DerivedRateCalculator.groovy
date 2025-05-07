class DerivedRateCalculator{
    def calculateDerivedAverageRate(float[] RateBid1,float[] RateAsk1,float[] RateBid2,float[] RateAsk2){
        float askSum = 0.0
        float bidSum = 0.0

        for(float bid1 : RateBid1){
            bidSum += bid1
        }
        bidSum /= RateBid1.length

        for(float ask1 : RateAsk1){
            askSum += ask1
        }
        askSum /= RateAsk1.length

        float mid = (bidSum + askSum) / 2.0


        float derivedBid = 0.0;
        for(float bid2 : RateBid2){
            derivedBid += bid2
        }
        derivedBid = mid * (derivedBid / RateBid2.length)

        float derivedAsk = 0.0
        for(float ask2 : RateAsk2){
            derivedAsk += ask2
        }
        derivedAsk = mid * (derivedAsk / RateAsk2.length)

        float[] derivedRateFields = new Float[2]
        derivedRateFields[0] = derivedBid
        derivedRateFields[1] = derivedAsk

        return derivedRateFields
    }
}