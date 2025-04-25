package com.toyotabackend.mainplatform.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private long id;
    private String rateName;
    private float bid;
    private float ask;
    private String rateUpdateTime;
}
