package com.toyota.toyotabackend.restapi.service;

import org.springframework.stereotype.Service;

@Service
public interface LoginDetailService {
    Boolean validateLogin(String receivedUsername, String receivedPassword);
}
