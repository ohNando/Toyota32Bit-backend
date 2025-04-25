package com.toyotabackend.mainplatform.Data_Provider;

import java.io.IOException;

/**
 * Interface for data provider implementations.
 * <p>
 * This interface defines the required operations for connecting to
 * and interacting with external rate data platforms, such as TCP or REST services.
 */
public interface DataProvider {

    /**
     * Connects to the given platform using provided credentials.
     *
     * @param platformName the name of the platform to connect to
     * @param username      the username for authentication
     * @param password      the password for authentication
     * @return true if connection is successful, false otherwise
     * @throws IOException if connection fails due to I/O errors
     */
    Boolean connect(String platformName, String username, String password) throws IOException;

    /**
     * Disconnects from the given platform.
     *
     * @param platformName the name of the platform to disconnect from
     * @param username      the username for authentication (if required)
     * @param password      the password for authentication (if required)
     * @return true if disconnection is successful, false otherwise
     */
    Boolean disConnect(String platformName, String username, String password);

    /**
     * Subscribes to a specific rate on the given platform.
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate to subscribe to
     * @throws IOException if subscription fails due to I/O errors
     */
    void subscribe(String platformName, String rateName) throws IOException;

    /**
     * Unsubscribes from a specific rate on the given platform.
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate to unsubscribe from
     */
    void unSubscribe(String platformName, String rateName);
}
