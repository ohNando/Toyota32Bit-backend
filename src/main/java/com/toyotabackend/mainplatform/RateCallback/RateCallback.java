package com.toyotabackend.mainplatform.RateCallback;

import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Entity.RateFields;
import com.toyotabackend.mainplatform.Entity.RateStatus;

import java.io.IOException;

public interface RateCallback {
    void onConnect(String platformName, Boolean status) throws IOException;
    void onDisConnect(String platformName, Boolean status);
    void onRateAvailable(String platformName, String rateName, RateDto dto);
    void onRateUpdate(String platformName, String rateName, RateFields rateFields);
    void onRateStatus(String platformName, String rateName, RateStatus rateStatus);
}

