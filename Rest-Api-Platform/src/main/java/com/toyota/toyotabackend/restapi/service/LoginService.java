package com.toyota.toyotabackend.restapi.service;

import com.toyota.toyotabackend.restapi.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    Boolean authenticate(UserDto userDto);
}
