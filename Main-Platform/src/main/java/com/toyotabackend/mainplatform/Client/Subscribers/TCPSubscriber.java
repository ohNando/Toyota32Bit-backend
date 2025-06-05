package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Mapper.RateMapper;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private List<String> subscribedRates;
    private CoordinatorInterface coordinator;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private boolean connectionStatus;

    private final Logger logger = LoggerFactory.getLogger("SubscriberLogger-TCP");

    /**
     * Constructs a TCPSubscriber with server address and port details.
     *
     * @param subscriberName name identifier for logging
     * @param _serverAddress the TCP server address
     * @param _serverPort    the TCP server port
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
     * Sets the coordinator reference to handle rate updates and status.
     *
     * @param coordinator the coordinator instance
     */
    @Override
    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    /**
     * Returns the current connection status.
     *
     * @return true if connected, false otherwise
     */
    @Override
    public boolean getConnectionStatus(){
        return this.connectionStatus; 
    }

    @Override
    public boolean checkPlatformName(String platformName){
        if (!platformName.equals("PF2")) {
            logger.warn("Invalid platform name: {}", platformName);
            this.connectionStatus = false;
            return false ;
        }
        return true;
    }

    /**
     * Connects to the TCP platform using username and password.
     *
     * @param platformName the platform identifier (must be "PF1")
     * @param username     the login username
     * @param password     the login password
     * @throws IOException if socket communication fails
     */
    @Override
    public void connect(String platformName, String username, String password) throws IOException {
        if(!checkPlatformName(platformName)) return;

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
        output.println(loginMessage);
        if(!socket.isConnected()){
            socket.close();
            logger.error("Failed to connect to {}:{}",serverAddress,serverPort);
        }
        logger.info("PF1 is connected to {} : {}",serverAddress,serverPort);
        coordinator.onConnect(platformName, connectionStatus);
        this.start();
    }

     /**
     * Disconnects from the TCP platform and closes all streams.
     *
     * @param platformName the platform identifier
     * @param username     the login username
     * @param password     the login password
     */
    @Override
    public void disConnect(String platformName, String username, String password) {
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
     * Subscribes to a rate by sending a subscription request to the server.
     *
     * @param platformName the platform identifier
     * @param rateName     the name of the rate to subscribe to
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
     * Unsubscribes from a rate by sending an unsubscribe request to the server.
     *
     * @param platformName the platform identifier
     * @param rateName     the name of the rate to unsubscribe from
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
     * Main loop to listen for incoming rate updates and notify the coordinator.
     */
    @Override
    public void run() {
        logger.info("Subscriber is starting.");
        String response;
        logger.info("CONNECTION STATUS = {}",this.connectionStatus);
        while(this.connectionStatus){
            try{
                response = input.readLine();
                logger.info(response);
                if(response ==  null){
                    logger.warn("NO RATE AVAILABLE");
                    continue;
                }
                RateDto dto = RateMapper.stringToDTO(response);
                if(dto != null){
                    logger.info("DTO INFO = {} {} {}",dto.getRateName(),dto.getBid(),dto.getAsk());

                    for(String subscribedRate : subscribedRates){
                        if(dto.getRateName().equals("PF1" + "_" + subscribedRate)){
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
                logger.error("PF1" + "Subscriber Error: {}", error.getMessage());
            }
        }
        logger.info("DISCONNECTING");
    }
}
