package com.toyota.toyotabackend.restapi.service;

import com.toyota.toyotabackend.restapi.entity.Response;
import com.toyota.toyotabackend.restapi.entity.User;
import org.springframework.stereotype.Component;

/**
 * Service interface for handling user authentication.
 * <p>
 * This service is responsible for authenticating users based on their provided credentials.
 * It defines a method for validating the login information of a user.
 * </p>
 */
@Component
public interface LoginService {

    /**
     * Authenticates a user based on the provided {@link User}.
     * <p>
     * This method checks the username and password of the user against stored credentials
     * and returns a boolean indicating whether the authentication was successful.
     * </p>
     *
     * @param user The data transfer object containing the user's login credentials (username and password).
     * @return {@code true} if the user is authenticated, {@code false} otherwise.
     */
    Response authenticate(User user);
}
