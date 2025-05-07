package com.toyotabackend.mainplatform.Coordinator;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;

/**
 * Callback interface to handle events related to rate data.
 * <p>
 * This interface defines methods to handle different events that can occur during
 * the rate subscription and processing lifecycle, such as connecting, disconnecting,
 * receiving new rate data, rate updates, and status changes.
 * Implementing classes must provide the necessary logic for handling these events.
 */
public interface CoordinatorInterface {
    /**
     * Called when a connection to a platform is established or lost.
     *
     * @param platformName the name of the platform
     * @param status       the connection status (true for connected, false for disconnected)
     * @throws IOException if an error occurs during the connection event handling
     */
    void onConnect(String platformName, Boolean status) throws IOException;

    /**
     * Called when the connection to a platform is disconnected.
     *
     * @param platformName the name of the platform
     * @param status       the disconnection status (true for disconnected, false for connection)
     */
    void onDisConnect(String platformName, Boolean status);

    /**
     * Called when a new rate is available for a specific platform and rate name.
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate
     * @param dto          the rate data transfer object containing the rate information
     */
    void onRateAvailable(String platformName, String rateName, RateDto dto);

    /**
     * Called when a rate is updated with new values.
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate
     * @param rateFields   the updated rate fields containing the new bid/ask values
     * @throws JsonProcessingException if an error occurs during the processing of the rate update
     */
    void onRateUpdate(String platformName, String rateName, RateDto dto);

    /**
     * Called when the status of a rate changes (e.g., available, unavailable, or error).
     *
     * @param platformName the name of the platform
     * @param rateName     the name of the rate
     * @param rateStatus   the updated status of the rate
     */
    RateStatus onRateStatus(String platformName, String rateName);
}
