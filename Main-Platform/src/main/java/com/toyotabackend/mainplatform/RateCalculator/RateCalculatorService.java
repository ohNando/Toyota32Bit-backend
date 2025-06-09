package com.toyotabackend.mainplatform.RateCalculator;

import groovy.lang.GroovyClassLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.RateService.RateService;

/**
 * Service class responsible for calculating rates, either raw or derived, 
 * using Groovy scripts to process the rate data.
 * The class leverages Groovy to execute scripts for rate calculation based on bid and ask values
 * and then returns the calculated rate in the form of a {@link RateDto}.
 */
public class RateCalculatorService {
    private GroovyClassLoader groovyClassLoader;
    private RateService service;
    private String[] rawRateNames;
    private String[] derivedRateNames;
    private final Logger logger = LogManager.getLogger(RateCalculatorService.class);

    private final String rawRateCalculatorScriptPath = System.getProperty("user.dir") + "/Scripts/RawRateCalculator.groovy";
    private final String derivedRateCalculaterScriptPath = System.getProperty("user.dir") + "/Scripts/DerivedRateCalculator.groovy";


    /**
     * Constructor to initialize the rate calculator service with necessary dependencies.
     * 
     * @param _service The service that provides rate data.
     * @param rawRates Array of raw rate names.
     * @param derivedRates Array of derived rate names.
     */
    public RateCalculatorService(RateService _service, String[] rawRates, String[] derivedRates) {
        this.service = _service;
        this.rawRateNames = rawRates;
        this.derivedRateNames = derivedRates;
        this.groovyClassLoader = new GroovyClassLoader(getClass().getClassLoader());
    }

    /**
     * Calculates the raw rates using the provided bid and ask values through a Groovy script.
     * 
     * @param bids Array of bid values.
     * @param asks Array of ask values.
     * @return An array of calculated raw rates.
     */
    public float[] calculateRawRatesWithMethod(float[] bids, float[] asks) {
        try {
            InputStream scriptStream = getClass().getResourceAsStream("/Scripts/RawRateCalculator.groovy");
            if(scriptStream == null) {
                logger.error("RawRateCalculator.groovy not found");
                throw new FileNotFoundException("RawRateCalculator.groovy not found");
            }
            Class<?> groovyClass = groovyClassLoader.parseClass(new InputStreamReader(scriptStream),"RawRateCalculator.groovy");
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculateAverageRate", float[].class, float[].class);
            return (float[]) method.invoke(groovyObject, bids, asks);
        } catch (Exception e) {
            logger.error("Error executing Groovy script 'RawRateCalculator'",e);
            throw new RuntimeException("Failed to execute raw rate calculating script",e);
        }
    }

    /**
     * Calculates the derived rates using bid and ask values from two different raw rates via a Groovy script.
     * 
     * @param bidRates1 Bid values of the first raw rate.
     * @param askRates1 Ask values of the first raw rate.
     * @param bidRates2 Bid values of the second raw rate.
     * @param askRates2 Ask values of the second raw rate.
     * @return An array of calculated derived rates.
     */
    public float[] calculateDerivedRatesWithMethod(float[] bidRates1, float[] askRates1,
                                                    float[] bidRates2, float[] askRates2) {
        try {
            InputStream scriptStream = getClass().getResourceAsStream("/Scripts/DerivedRateCalculator.groovy");
            if(scriptStream == null) {
                logger.error("DerivedRateCalculator.groovy not found");
                throw new FileNotFoundException("DerivedRateCalculator.groovy not found");
            }
            Class<?> groovyClass = groovyClassLoader.parseClass(new InputStreamReader(scriptStream),"DerivedRateCalculator.groovy");
            Object groovyObject = groovyClass.newInstance();
            Method method = groovyClass.getMethod("calculateDerivedAverageRate", float[].class, float[].class, float[].class, float[].class);
            return (float[]) method.invoke(groovyObject, bidRates1, askRates1, bidRates2, askRates2);
        } catch (Exception e) {
            logger.error("Error executing Groovy script 'derivedRateCalculator'",e);
            throw new RuntimeException("Failed to execute derived rate calculating script",e);
        }
    }

    /**
     * Calculates the rate for a given rate name. It will determine if the rate is a raw or derived rate
     * and invoke the appropriate calculation method.
     * 
     * @param rateName The name of the rate to calculate.
     * @return The calculated {@link RateDto} for the given rate name.
     */
    public RateDto calculateRate(String rateName) {
        for (String rawRate : rawRateNames) {
            if (rawRate.equals(rateName)) {
                return calculateRawRate(rateName);
            }
        }
        for (String derivedRate : derivedRateNames) {
            if (derivedRate.equals(rateName)) {
                return calculateDerivedRate(rateName);
            }
        }
        return null;
    }

    /**
     * Calculates a raw rate based on the provided rate name.
     * 
     * @param rateName The raw rate name to calculate.
     * @return The calculated raw {@link RateDto}.
     */
    public RateDto calculateRawRate(String rateName) {
        logger.info("Calculating raw rate : {}", rateName);
        List<RateDto> rawRateList = service.getRawRateContains(rateName);
        if (rawRateList.isEmpty()) {
            return null;
        }

        float[] bids = new float[rawRateList.size()];
        float[] asks = new float[rawRateList.size()];

        for (int i = 0; i < rawRateList.size(); i++) {
            bids[i] = rawRateList.get(i).getBid();
        }
        for (int i = 0; i < rawRateList.size(); i++) {
            asks[i] = rawRateList.get(i).getAsk();
        }

        float[] rateFields = calculateRawRatesWithMethod(bids, asks);
        if(rateFields == null || rateFields.length < 2) {
            logger.warn("Raw rate calculation script failed or returned invalid data for" +
                    "rate {} .Received array length {}", rateName, (rateFields != null ? rateFields.length : 0));
            return null;
        }
        logger.info("Raw Rate calculated : {}", rateName);

        return new RateDto(rateName, rateFields[0], rateFields[1], rawRateList.getFirst().getRateUpdateTime());
    }

    /**
     * Calculates a derived rate based on two raw rates (split by currency) using the provided rate name.
     * 
     * @param rateName The derived rate name to calculate.
     * @return The calculated derived {@link RateDto}.
     */
    public RateDto calculateDerivedRate(String rateName) {
        logger.info("Calculating derived rate : {}", rateName);
        List<RateDto> rawRateList1 = service.getRawRateContains(rateName.substring(0, 3));
        List<RateDto> rawRateList2 = service.getRawRateContains(rateName.substring(3, 6));

        if (rawRateList1.isEmpty() || rawRateList2.isEmpty()) {
            logger.error("Raw rates are missing for calculation : {}", rateName);
            return null;
        }

        float[] rateBids1 = new float[rawRateList1.size()];
        float[] rateAsks1 = new float[rawRateList1.size()];
        float[] rateBids2 = new float[rawRateList2.size()];
        float[] rateAsks2 = new float[rawRateList2.size()];

        for (int i = 0; i < rawRateList1.size(); i++) {
            rateBids1[i] = rawRateList1.get(i).getBid();
        }
        for (int i = 0; i < rawRateList2.size(); i++) {
            rateBids2[i] = rawRateList2.get(i).getBid();
        }

        for (int i = 0; i < rawRateList1.size(); i++) {
            rateAsks1[i] = rawRateList1.get(i).getAsk();
        }
        for (int i = 0; i < rawRateList2.size(); i++) {
            rateAsks2[i] = rawRateList2.get(i).getAsk();
        }

        float[] rateFields = calculateDerivedRatesWithMethod(rateBids1, rateAsks1, rateBids2, rateAsks2);
        if(rateFields == null || rateFields.length < 2){
            logger.warn("Derived rate calculation script failed or returned invalid data for" +
                    "rate {}.Received array length {}", rateName, (rateFields != null ? rateFields.length : 0));
            return null;
        }

        logger.info("Derived rate calculated : {}", rateName);
        return new RateDto(rateName, rateFields[0], rateFields[1], rawRateList2.getFirst().getRateUpdateTime());
    }
}
