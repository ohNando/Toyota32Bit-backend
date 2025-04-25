package com.toyotabackend.mainplatform.ClassLoader;

import com.toyotabackend.mainplatform.Data_Provider.DataProvider;

import java.io.FileInputStream;
import java.util.Properties;

public class LoadSubscriberClass {
    public static void loadSubscriber() {
        try{
            Properties properties = new Properties();
            properties.load(new FileInputStream("application.properties"));
            String subscriber1Class = properties.getProperty("subscriber1.class");
            String subscriber2Class = properties.getProperty("subscriber2.class");

            DataProvider subscriber1 = (DataProvider) Class.forName(subscriber1Class).newInstance();
            DataProvider subscriber2 = (DataProvider) Class.forName(subscriber2Class).newInstance();
        }catch (Exception e){}
    }
}
