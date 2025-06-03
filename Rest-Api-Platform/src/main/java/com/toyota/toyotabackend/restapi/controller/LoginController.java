package com.toyota.toyotabackend.restapi.controller;

import com.toyota.toyotabackend.restapi.entity.Response;
import com.toyota.toyotabackend.restapi.entity.User;
import com.toyota.toyotabackend.restapi.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * This method processes login requests by accepting a {@link User} containing
     * the user's credentials. It calls the {@link LoginService#authenticate(User)}
     * method to verify the credentials and returns a response indicating success or failure.
     * </p>
     *
     * @param user The user data transfer object containing the login credentials.
     * @return A {@link ResponseEntity} containing the login status and message.
     */
    @PostMapping("/login")
    public Response login(@RequestBody User user) {
        return loginService.authenticate(user);
    }
}