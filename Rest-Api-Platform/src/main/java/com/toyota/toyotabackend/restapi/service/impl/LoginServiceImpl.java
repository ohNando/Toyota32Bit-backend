package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.UserDto;
import com.toyota.toyotabackend.restapi.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Value("${login.user.username}")
    private String username;
    @Value("${login.user.password}")
    private String password;

    @Override
    public Boolean authenticate(UserDto userDto) {
        return username.equals(userDto.getUsername()) && password.equals(userDto.getPassword());
    }
}
