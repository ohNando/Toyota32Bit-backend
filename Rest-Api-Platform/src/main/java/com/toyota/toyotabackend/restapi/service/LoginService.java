package com.toyota.toyotabackend.restapi.service;

import com.toyota.toyotabackend.restapi.dto.ResponseDto;
import com.toyota.toyotabackend.restapi.dto.UserDto;
import org.springframework.stereotype.Service;

/**
 * Service interface for handling user authentication.
 * <p>
 * This service is responsible for authenticating users based on their provided credentials.
 * It defines a method for validating the login information of a user.
 * </p>
 */
@Service
public interface LoginService {

    /**
     * Authenticates a user based on the provided {@link UserDto}.
     * <p>
     * This method checks the username and password of the user against stored credentials
     * and returns a boolean indicating whether the authentication was successful.
     * </p>
     *
     * @param userDto The data transfer object containing the user's login credentials (username and password).
     * @return {@code true} if the user is authenticated, {@code false} otherwise.
     */
    ResponseDto authenticate(UserDto userDto);  
}
