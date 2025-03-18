package com.toyotabackend.mainplatform.Data_Provider;

public interface Data_Provider {
    void connect(String platformName, String userid, String password);
    void disConnect(String platformName, String userid, String password);
    void subscribe(String platformName, String rateName);
    void unSubscribe(String platformName, String rateName);
}
