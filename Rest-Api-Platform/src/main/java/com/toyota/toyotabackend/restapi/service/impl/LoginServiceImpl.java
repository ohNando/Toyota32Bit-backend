package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.UserDto;
import com.toyota.toyotabackend.restapi.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link LoginService} interface that provides user authentication functionality.
 * <p>
 * This service compares the credentials passed by the user with those stored in the application's properties.
 * If the credentials match, authentication is successful.
 * </p>
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Value("${login.user.username}")
    private String username;

    @Value("${login.user.password}")
    private String password;

    /**
     * Authenticates a user by comparing the provided credentials with the stored ones.
     *
     * @param userDto the user credentials to authenticate.
     * @return {@code true} if the provided username and password match the stored ones; {@code false} otherwise.
     */
    @Override
    public Boolean authenticate(UserDto userDto) {
        return username.equals(userDto.getUsername()) && password.equals(userDto.getPassword());
    }
}
