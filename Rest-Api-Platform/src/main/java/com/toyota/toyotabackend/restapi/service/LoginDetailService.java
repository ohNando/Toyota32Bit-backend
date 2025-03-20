package com.toyota.toyotabackend.restapi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface LoginDetailService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
