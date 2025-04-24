package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.service.LoginDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginDetailServiceImpl implements LoginDetailService {
    @Value("${login.user.username}")
    private String username_login;
    @Value("${login.user.password}")
    private String password_login;

    @Override
    public Boolean validateLogin(String receivedUsername, String receivedPassword) {
        return username_login.equals(receivedUsername) && password_login.equals(receivedPassword);
    }
}
