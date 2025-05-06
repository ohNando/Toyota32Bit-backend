package com.toyotabackend.mainplatform.RateCalculator;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;

public class RateCalculatorService {
    private final GroovyClassLoader groovyClassLoader;

    public RateCalculatorService(GroovyClassLoader groovyClassLoader) {
        this.groovyClassLoader = groovyClassLoader;
    }

    public float calculate(float bid1,float ask1,float bid2,float ask2) throws Exception {
        String scriptPath = "src/main/Scripts/Calculator/RateCalculator.groovy";
        Class groovyClass = groovyClassLoader.parseClass(new File(scriptPath));
        Object instance = groovyClass.getDeclaredConstructor().newInstance();

        return (float) groovyClass.getMethod("calculateAverageRate",float.class,float.class,float.class,float.class)
                .invoke(instance,bid1,ask1,bid2,ask2);
    }
}
