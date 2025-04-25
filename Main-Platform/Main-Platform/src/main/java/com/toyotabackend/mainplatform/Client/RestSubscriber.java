package com.toyotabackend.mainplatform.Client;

import com.toyotabackend.mainplatform.Data_Provider.DataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RestSubscriber implements DataProvider {  //Rest Api
    @Value("${client.Rest_Api.baseUrl}")
    private String baseUrl;
    @Value("${client.Rest_Api.loginUrl}")
    private String loginUrl;
    private RestTemplate restTemplate;

    public RestSubscriber() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Boolean connect(String platformName, String username, String password) {
        if(!platformName.equals("PF2")){
            System.out.println("(-)|Invalid Platform Name!");
            return false;
        }
        String request = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
            String responseStatus = response.getStatusCode().toString();

            if(responseStatus.equals("200")){   //OK
                System.out.println("(+)|Successfully connected to the server!");
                return true;
            }else{
                System.out.println("(-)|Failed to connect to the server with status code: " + responseStatus);
                return false;
            }
        }catch (Exception e){
            System.out.println("(!)|Error during login " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean disConnect(String platformName, String username, String password) {
        if(!platformName.equals("PF2")){
            System.out.println("(-)|Invalid Platform Name!");
            return false;
        }
        System.out.println("(+)|Successfully disconnected from the server!");
        return true;
    }

    @Override
    public String subscribe(String platformName, String rateName) {
        //burada kaldin string donduurp dondurmedigini dusun
        //tcp de birden fazla RESPONSE donuoyr bunu da dusun nasil alabilecegini vs.

        return platformName;
    }

    @Override
    public void unSubscribe(String platformName, String rateName) {

    }
}
