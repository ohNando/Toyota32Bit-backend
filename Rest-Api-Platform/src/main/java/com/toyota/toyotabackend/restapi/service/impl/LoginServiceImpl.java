package com.toyota.toyotabackend.restapi.service.impl;

import com.toyota.toyotabackend.restapi.entity.Response;
import com.toyota.toyotabackend.restapi.entity.User;
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

    private User tempUser;

    public LoginServiceImpl(){
        tempUser = new User("admin","12345");
    }
    /**
     * Authenticates a user by comparing the provided credentials with the stored ones.
     *
     * @param user the user credentials to authenticate.
     * @return {@code true} if the provided username and password match the stored ones; {@code false} otherwise.
     */
    @Override
    public Response authenticate(User user) {
        if(user.getUsername().equals(tempUser.getUsername()) &&
        user.getPassword().equals(tempUser.getPassword())){
            return new Response("success","Login success!");
        }
        return new Response("fail","Wrong password or username!");
    }
}
