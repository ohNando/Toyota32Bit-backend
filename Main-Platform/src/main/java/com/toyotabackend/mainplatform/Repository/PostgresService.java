package com.toyotabackend.mainplatform.Repository;

import com.toyotabackend.mainplatform.Entity.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostgresService {
    @Autowired
    private RateRepository rateRepository;

    public void saveRate(Rate rate){
        rateRepository.save(rate);
    }
}
