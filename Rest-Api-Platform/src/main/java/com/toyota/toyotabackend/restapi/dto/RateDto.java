package com.toyota.toyotabackend.restapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private String rateName;
    private double bid;
    private double ask;
    private String rateUpdateTime;
}
