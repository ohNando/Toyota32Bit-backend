package com.toyota.toyotabackend.restapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RateDto {
    private String rateName;
    private float bid;
    private float ask;
    private Instant timestamp;
}
