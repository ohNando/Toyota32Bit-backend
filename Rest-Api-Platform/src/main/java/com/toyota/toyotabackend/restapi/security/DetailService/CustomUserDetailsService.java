package com.toyota.toyotabackend.restapi.security.DetailService;

import com.toyota.toyotabackend.restapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Value("${login.user.username}")
    private String validUsername;
    @Value("${login.user.password}")
    private String validPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException{
        if(!validUsername.equals(username) && !validPassword.equals(username)){
            throw new UserNotFoundException("Username not found :" + username);
        }
        return User.builder().
                username(username).
                password(validPassword).
                roles("USER").
                build();
    }
}
