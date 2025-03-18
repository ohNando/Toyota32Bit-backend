package com.toyota.toyotabackend.restapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface LoginDetailService {
    public UserDetails loadUserByUsername(String username);
}
