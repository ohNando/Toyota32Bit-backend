package com.toyota.toyotabackend.restapi.controller;

import com.toyota.toyotabackend.restapi.dto.RateDto;
import com.toyota.toyotabackend.restapi.service.RateService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/rates")
public class RateController {
    private final RateService rateService;
    
    @GetMapping(path = "/{rateName}")
    public RateDto getRates(@PathVariable String rateName){
        return rateService.getRate(rateName);
    }
}
