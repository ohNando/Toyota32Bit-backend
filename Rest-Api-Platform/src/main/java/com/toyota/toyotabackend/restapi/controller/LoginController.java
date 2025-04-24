package com.toyota.toyotabackend.restapi.controller;

import com.toyota.toyotabackend.restapi.dto.UserDto;
import com.toyota.toyotabackend.restapi.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody UserDto userDto) {
        Map<String,String> response = new HashMap<>();
        if(loginService.authenticate(userDto)) {
            response.put("Message", "(+)|Login successful");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }else{
            response.put("Message", "(-)|Username or password incorrect");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}