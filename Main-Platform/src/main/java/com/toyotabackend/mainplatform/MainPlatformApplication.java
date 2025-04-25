package com.toyotabackend.mainplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application for the main platform.
 * <p>
 * This class is responsible for starting the Spring Boot application context.
 * The {@link SpringApplication} class is used to bootstrap the application.
 */
@SpringBootApplication
public class MainPlatformApplication {

    /**
     * The main method which serves as the entry point for the Spring Boot application.
     * <p>
     * This method triggers the Spring Boot application to start by invoking the
     * {@link SpringApplication#run(Class, String...)} method.
     *
     * @param args the command-line arguments passed to the application, which are
     *             forwarded to the Spring Boot context.
     */
    public static void main(String[] args) {
        SpringApplication.run(MainPlatformApplication.class, args);
    }
}
