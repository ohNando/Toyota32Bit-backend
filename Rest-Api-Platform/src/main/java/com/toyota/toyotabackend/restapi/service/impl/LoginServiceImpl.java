package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.dto.ResponseDto;
import com.toyota.toyotabackend.restapi.dto.UserDto;
import com.toyota.toyotabackend.restapi.service.LoginService;

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

    private UserDto tempUser;

    public LoginServiceImpl(){
        tempUser = new UserDto("admin","12345");
    }
    /**
     * Authenticates a user by comparing the provided credentials with the stored ones.
     *
     * @param userDto the user credentials to authenticate.
     * @return {@code true} if the provided username and password match the stored ones; {@code false} otherwise.
     */
    @Override
    public ResponseDto authenticate(UserDto userDto) {
        if(userDto.getUsername().equals(tempUser.getUsername()) && 
        userDto.getPassword().equals(tempUser.getPassword())){
            return new ResponseDto("success","Login success!");
        }
        return new ResponseDto("fail","Wrong password or username!");
    }
}
