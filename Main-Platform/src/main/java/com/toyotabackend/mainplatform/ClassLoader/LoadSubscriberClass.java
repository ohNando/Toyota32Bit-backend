package com.toyotabackend.mainplatform.ClassLoader;

import com.toyotabackend.mainplatform.Data_Provider.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class responsible for loading subscriber classes dynamically based on configuration.
 * <p>
 * This class reads the class names of the subscribers from the application properties
 * and dynamically loads the subscriber classes using reflection. It is used to provide
 * flexibility in adding new subscriber implementations without modifying the code.
 * </p>
 */
public class LoadSubscriberClass {

    /**
     * Loads the subscriber classes dynamically from the configuration.
     * <p>
     * This method reads the class names of two subscribers from the application properties
     * file and then creates instances of those classes using reflection. The created instances
     * are then cast to the {@link DataProvider} interface type.
     * </p>
     */
    public static void loadSubscriber() {
        Properties properties = new Properties();

        // Load properties from the configuration file
        try (FileInputStream inputStream = new FileInputStream("application.properties")) {
            properties.load(inputStream);

            String subscriber1Class = properties.getProperty("subscriber1.class");
            String subscriber2Class = properties.getProperty("subscriber2.class");

            // Check if properties are properly loaded
            if (subscriber1Class == null || subscriber2Class == null) {
                throw new IllegalArgumentException("Subscriber class names are not found in the properties file.");
            }

            // Dynamically load the subscriber classes using reflection
            DataProvider subscriber1 = (DataProvider) Class.forName(subscriber1Class).newInstance();
            DataProvider subscriber2 = (DataProvider) Class.forName(subscriber2Class).newInstance();

            // Optionally: Initialize or use subscriber instances (subscriber1, subscriber2)

        } catch (IOException e) {
            System.err.println("Error loading properties file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Error instantiating subscriber classes: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid configuration: " + e.getMessage());
        }
    }
}
