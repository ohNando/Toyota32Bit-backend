package com.toyota.toyotabackend.restapi.controller;

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

    private final LoginService loginService;

    /**
     * Constructs a LoginController with the provided LoginService.
     *
     * @param loginService The service used to authenticate users.
     */
    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

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
    public ResponseEntity<Map<String,String>> login(@RequestBody UserDto userDto) {
        Map<String,String> response = new HashMap<>();

        if (loginService.authenticate(userDto)) {
            response.put("Message", "(+)|Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("Message", "(-)|Username or password incorrect");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
