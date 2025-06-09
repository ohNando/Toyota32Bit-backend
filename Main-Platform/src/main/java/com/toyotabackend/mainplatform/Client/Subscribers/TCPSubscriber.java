package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Mapper.RateMapper;

import lombok.Getter;
import lombok.Setter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TCPSubscriber is a data provider implementation that connects to a TCP server
 * to receive rate information based on subscriptions.
 * <p>
 * It runs in a separate thread and listens for real-time data sent by the server
 * after establishing a connection and sending a login request.
 * </p>
 * <p>
 * This class is designed specifically for use with platform "PF1".
 * </p>
 */
@Getter
@Setter
public class TCPSubscriber extends Thread implements SubscriberInterface { //Subscriber 2
    private String serverAddress;
    private int serverPort;
    private String subscriberName;

    private String username;
    private String password;

    private List<String> subscribedRates;
    private CoordinatorInterface coordinator;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private boolean connectionStatus;

    private final Logger logger = LogManager.getLogger(TCPSubscriber.class);

    /**
     * Constructs a TCPSubscriber with server address and port details.
     *
     * @param subscriberName name identifier for logging and platform validation
     * @param _serverAddress the TCP server IP or hostname
     * @param _serverPort    the TCP server port number
     */
    public TCPSubscriber(String subscriberName,String _serverAddress,int _serverPort){
        logger.info("Initializing {} Subscriber", subscriberName);
        this.subscriberName = subscriberName;
        this.subscribedRates = new ArrayList<>();
        this.serverAddress = _serverAddress;
        this.serverPort = _serverPort;
        this.setConnectionStatus(false);
        logger.info("{} Subscriber initialized!", subscriberName);
    }

    /**
     * Sets the coordinator instance to handle rate update callbacks and connection events.
     * @param coordinator the coordinator interface implementation
     */
    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    /**
     * Returns the current connection status.
     * @return true if connected, false otherwise
     */
    @Override
    public boolean getConnectionStatus(){
        return this.connectionStatus; 
    }

    /**
     * Checks if the given platform name matches this subscriber's name.
     *
     * @param platformName the platform name to verify
     * @return true if names match, false otherwise
     */
    @Override
    public boolean checkPlatformName(String platformName){
        if (!platformName.equals(subscriberName)) {
            logger.warn("Invalid platform name: {}", platformName);
            this.connectionStatus = false;
            return false ;
        }
        return true;
    }

    /**
     * Connects to the TCP server using the specified credentials.
     * Establishes socket, opens streams, sends login request,
     * and starts the subscriber thread on success.
     *
     * @param platformName the platform identifier
     * @param username     the login username
     * @param password     the login password
     * @throws IOException if network or socket errors occur
     */
    @Override
    public void connect(String platformName, String username, String password) throws IOException {
        if(!checkPlatformName(platformName)) return;

        this.username = username;
        this.password = password;

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
            this.setConnectionStatus(false);
            return;
        }
        
        this.setConnectionStatus(true);
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Trying to login to {} : {}",serverAddress,serverPort);

        String loginMessage = String.format("login|%s|%s", username, password);
        if(socket == null || !socket.isConnected()){
            logger.error("Failed to connect to {}:{}",serverAddress,serverPort);
            return;
        }
        output.println(loginMessage);

        logger.info("{} is connected to {} : {}",subscriberName,serverAddress,serverPort);
        coordinator.onConnect(platformName, connectionStatus);
        this.start();
    }

    /**
     * Disconnects from the TCP server by closing socket and streams,
     * updates connection status, and notifies the coordinator.
     *
     * @param platformName the platform identifier
     */
    @Override
    public void disConnect(String platformName) {
        if(!checkPlatformName(platformName)) return;

        try{
            this.input.close();
            this.output.close();
            this.setConnectionStatus(false);
            coordinator.onDisConnect(platformName,connectionStatus);
            this.socket.close();    
            logger.info("{} disconnected from {} : {}",platformName,serverAddress,serverPort);
        }catch(IOException error){
            logger.error("Failed to close socket : {}",error.getMessage());
        }
        
    }

    /**
     * Sends a subscription request to the server for the specified rate.
     *
     * @param platformName the platform identifier
     * @param rateName     the rate to subscribe to
     */
    @Override
    public void subscribe(String platformName, String rateName) {
        if(!checkPlatformName(platformName)) return;
        
        logger.info("{} is subscribing to {}",platformName,rateName);
        this.output.println("subscribe|" + platformName + "_" + rateName);
        this.subscribedRates.add(rateName);
        logger.info("{} is subscribed to {}",platformName,rateName);
    }

    /**
     * Sends an unsubscribe request to the server for the specified rate.
     *
     * @param platformName the platform identifier
     * @param rateName     the rate to unsubscribe from
     */
    @Override
    public void unSubscribe(String platformName, String rateName) {
        if(!checkPlatformName(platformName)) return;

        logger.info("{} is unsubscribing from {}",platformName,rateName);
        this.output.println("unsubscribe|" + platformName + "_" + rateName);
        this.subscribedRates.remove(rateName);
        logger.info("{} is unsubscribed from {}",platformName,rateName);
    }

    /**
     * Main run loop which listens for incoming rate updates from the TCP server.
     * On receiving data, parses it into RateDto, filters subscribed rates,
     * and notifies the coordinator of the rate status and updates.
     * Handles reconnection attempts on connection loss.
     */
    @Override
    public void run() {
        logger.info("Subscriber is starting.");
        int retryCount = 0;
        final int maxRetries = 3;

        String response;
        while(!Thread.currentThread().isInterrupted()){
            if(!connectionStatus){
                if(retryCount >= maxRetries){
                    logger.error("Max tries reached. Disconnecting safely.");
                    disConnect(subscriberName);
                    break;
                }
                logger.warn("Trying to reconnect... attempt {}/{}", retryCount+1, maxRetries);
                try{
                    this.connect(subscriberName,username,password);

                    if(connectionStatus){
                        logger.info("Reconnected successfully");
                        retryCount = 0;
                    }else{
                        retryCount++;
                        Thread.sleep(1000);
                        continue;
                    }
                }catch(IOException |  InterruptedException e){
                    logger.error("Reconnect attemp failed {}",e.getMessage());
                    retryCount++;
                    try{ Thread.sleep(1000); }
                    catch(InterruptedException error){ logger.error("Reconnect interrupted {}",e.getMessage()); }
                    continue;
                }

            }

            try{
                response = input.readLine();
                logger.info(response);
                if(response ==  null){
                    logger.warn("NO RATE AVAILABLE - Possibly disconnected");
                    setConnectionStatus(false);
                    coordinator.onDisConnect(subscriberName,connectionStatus);
                    continue;
                }

                RateDto dto = RateMapper.stringToDTO(response);
                if(dto != null){
                    for(String subscribedRate : subscribedRates){
                        if(dto.getRateName().equals(subscriberName + "_" + subscribedRate)){
                            switch (coordinator.onRateStatus(this.subscriberName, dto.getRateName())) {
                                case RateStatus.NOT_AVAILABLE:
                                    logger.info("Fetched rate in NOT_AVAILABLE: {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());
                                    coordinator.onRateAvailable(this.subscriberName,dto.getRateName(),dto);
                                    break;
                                case RateStatus.AVAILABLE:
                                    logger.info("Fetched rate in AVAILABLE: {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());
                                    coordinator.onRateUpdate(this.subscriberName, dto.getRateName(), dto);
                                    break;
                                case RateStatus.UPDATED:
                                    logger.info("Fetched rate in UPDATED: {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());
                                    coordinator.onRateUpdate(this.subscriberName,dto.getRateName(),dto);
                                    break;
                            }
                        }
                    }
                }

            }catch(IOException error){
                logger.error("{} Subscriber Error: {}",subscriberName, error.getMessage());
                this.setConnectionStatus(false);
                coordinator.onDisConnect(subscriberName,connectionStatus);
            }
        }
    }
}
