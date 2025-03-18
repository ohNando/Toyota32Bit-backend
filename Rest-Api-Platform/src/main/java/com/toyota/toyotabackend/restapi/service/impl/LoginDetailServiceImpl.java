package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.exception.UserNotFoundException;
import com.toyota.toyotabackend.restapi.service.LoginDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginDetailServiceImpl implements LoginDetailService {
    @Value("${login.user.username}")
    private String username_login;

    @Value("${login.user.password}")
    private String password_login;
    @Override
    public UserDetails loadUserByUsername(String username) {
        if(!username.equals(username_login)) {
            throw new UserNotFoundException("Username not found : " + username_login);
        }
        return User.builder()
                .username(username_login)
                .password("{noop}" + password_login)
                .build();
    }
}
