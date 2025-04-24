package com.toyota.toyotabackend.restapi.controller;

import com.toyota.toyotabackend.restapi.service.LoginDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginDetailService loginDetailService;
    @Value("${login.user.username}")
    private String username;

    @Value("${login.user.password}")
    private String password;

    public LoginController(LoginDetailService loginDetailService) {
        this.loginDetailService = loginDetailService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String receivedUsername,@RequestParam String receivedPassword) {
        if(loginDetailService.validateLogin(receivedUsername, receivedPassword)) {
            return ResponseEntity.status(HttpStatus.OK).body("(+)|Login successful");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("(-)|Username or password incorrect");
        }
    }
}