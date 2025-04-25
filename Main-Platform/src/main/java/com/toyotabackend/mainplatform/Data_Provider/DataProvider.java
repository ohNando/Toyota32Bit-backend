package com.toyotabackend.mainplatform.Data_Provider;

import java.io.IOException;

public interface DataProvider {
    Boolean connect(String platformName, String username, String password) throws IOException;
    Boolean disConnect(String platformName, String username, String password);
    String subscribe(String platformName, String rateName) throws IOException;
    void unSubscribe(String platformName, String rateName);
}
