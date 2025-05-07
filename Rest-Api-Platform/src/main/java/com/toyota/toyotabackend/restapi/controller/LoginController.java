package com.toyota.toyotabackend.restapi.controller;

import com.toyota.toyotabackend.restapi.dto.ResponseDto;
import com.toyota.toyotabackend.restapi.dto.UserDto;
import com.toyota.toyotabackend.restapi.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A REST controller that handles user authentication requests.
 * This class exposes an API endpoint for user login and uses the LoginService
 * to authenticate users based on their credentials.
 */
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * Endpoint for user login.
     * <p>
     * This method processes login requests by accepting a {@link UserDto} containing
     * the user's credentials. It calls the {@link LoginService#authenticate(UserDto)}
     * method to verify the credentials and returns a response indicating success or failure.
     * </p>
     *
     * @param userDto The user data transfer object containing the login credentials.
     * @return A {@link ResponseEntity} containing the login status and message.
     */
    @PostMapping("/login")
    public ResponseDto login(@RequestBody UserDto userDto) {
        return loginService.authenticate(userDto);
    }
}
