package com.toyotabackend.mainplatform.Coordinator;

import com.toyotabackend.mainplatform.ClassLoader.LoadJarFile;
import org.springframework.beans.factory.annotation.Value;

public class Coordinator {
    @Value("${rate.server.tcp.jarPath}")
    private String tcpJarPath;
    @Value("${rate.server.tcp.mainPath}")
    private String tcpMainPath;
    @Value("${rate.server.rest.jarPath}")
    private String restJarPath;
    @Value("${rate.server.rest.mainPath}")
    private String restMainPath;

    public Coordinator() {
        loadJar(); //Loading rate servers
    }

    private void loadJar(){
        try{
            LoadJarFile.loadJarFile(tcpJarPath,tcpMainPath); //tcp server
            LoadJarFile.loadJarFile(restJarPath,restMainPath); // rest api
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
