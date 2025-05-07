package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Mapper.RateMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component
public class TCPSubscriber extends Thread implements SubscriberInterface { //Subscriber 2
    @Value("${client.TCP.serverAddress}")
    private String serverAddress;
    @Value("${client.TCP.port}")
    private int serverPort;

    private List<String> subscribedRates;
    private CoordinatorInterface coordinator;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private boolean connectionStatus;

    private final Logger logger = LogManager.getLogger("SubscriberLogger");


    public TCPSubscriber(String subscriberName){
        logger.info("Initializing" +subscriberName + "Subscriber");
        this.subscribedRates = new ArrayList<>();
        logger.info(subscriberName + "Subscriber initialized!");
    }

    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    @Override
    public void connect(String platformName, String username, String password) throws IOException {
        if (!platformName.equals("PF1")) {
            logger.warn("Invalid platform name: {}", platformName);
            return;
        }
        try{
            socket = new Socket(serverAddress, serverPort);
        }catch(ConnectException error){
            try{
                if(socket != null && !socket.isClosed()){
                    socket.close();
                }
            }catch(IOException e){
                logger.error("Error while closing the socket :",e);
            }

            logger.error("Failed to connect {}",serverAddress);
            this.connectionStatus = false;
            return;
        }
        
        this.connectionStatus = true;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Trying to login to {} : {}",serverAddress,serverPort);

        String loginMessage = String.format("login|%s|%s", username, password);
        output.println(loginMessage);
        if(!socket.isConnected()){
            socket.close();
            logger.error("Failed to connect to {}:{}",serverAddress,serverPort);
        }
        logger.info("PF1 is connected to {} : {}",serverAddress,serverPort);
        coordinator.onConnect(platformName, connectionStatus);
        this.start();
    }

    @Override
    public void disConnect(String platformName, String username, String password) {
        if (!platformName.equals("PF1")) {
            logger.warn("Invalid platform name: {}", platformName);
            return;
        }
        try{
            this.input.close();
            this.output.close();
            this.connectionStatus = false;
            coordinator.onDisConnect(platformName,connectionStatus);
            this.socket.close();    
            logger.info("{} disconnected from {} : {}",platformName,serverAddress,serverPort);
        }catch(IOException error){
            logger.error("Failed to close socket : ",error.getMessage());
        }
        
    }

    @Override
    public void subscribe(String platformName, String rateName) {
        if (!platformName.equals("PF1")) {
            logger.warn("Invalid platform name: {}", platformName);
            return;
        }
        
        logger.info("{} is subscribing to {}",platformName,rateName);
        this.output.println("subscribe|" + platformName + "_" + rateName);
        this.subscribedRates.add(rateName);
        logger.info("{} is subscribed to {}",platformName,rateName);
    }

    @Override
    public void unSubscribe(String platformName, String rateName) {
        if (!platformName.equals("PF1")) {
            logger.warn("Invalid platform name: {}", platformName);
            return;
        }
        logger.info("{} is unsubscribing from {}",platformName,rateName);
        this.output.println("unsubscribe|" + platformName + "_" + rateName);
        this.subscribedRates.remove(rateName);
        logger.info("{} is unsubscribed from {}",platformName,rateName);
    }

    @Override
    public void run() {
        String response;
        while(connectionStatus){
            try{
                response = input.readLine();
                if(response == null){
                    continue;
                }
                RateDto dto = RateMapper.stringToDTO(response);
                for(String subscribedRate : subscribedRates){
                    if(dto.getRateName().equals("PF1" + "_" + subscribedRate)){
                        switch (coordinator.onRateStatus("PF1", dto.getRateName())) {
                            case RateStatus.NOT_AVAILABLE:
                                coordinator.onRateAvailable("PF1",dto.getRateName(),dto);
                                break;
                            case RateStatus.AVAILABLE:
                                coordinator.onRateUpdate("PF1",dto.getRateName(),dto);
                                break;
                            case RateStatus.UPDATED:
                                coordinator.onRateUpdate("PF1",dto.getRateName(),dto);
                                break;
                        }
                    }
                }
            }catch(IOException error){
                logger.error("PF1" + "Subscriber Error: {}", error.getMessage());
            }
        }
    }
}
