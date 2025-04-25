package com.toyotabackend.mainplatform.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RateFields {
    private float bid;
    private float ask;
    private String timestamp;
}
