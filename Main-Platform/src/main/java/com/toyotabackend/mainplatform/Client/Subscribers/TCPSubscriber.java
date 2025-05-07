package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Mapper.RateMapper;

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
public class TCPSubscriber extends Thread implements SubscriberInterface { //Subscriber 2
    private String serverAddress;
    private int serverPort;

    private List<String> subscribedRates;
    private CoordinatorInterface coordinator;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private boolean connectionStatus;

    private final Logger logger = LogManager.getLogger("SubscriberLogger");

    /**
     * Constructs a TCPSubscriber with server address and port details.
     *
     * @param subscriberName name identifier for logging
     * @param _serverAddress the TCP server address
     * @param _serverPort    the TCP server port
     */
    public TCPSubscriber(String subscriberName,String _serverAddress,int _serverPort){
        logger.info("Initializing" +subscriberName + "Subscriber");
        this.subscribedRates = new ArrayList<>();
        this.serverAddress = _serverAddress;
        this.serverPort = _serverPort;
        logger.info(subscriberName + "Subscriber initialized!");
    }

    /**
     * Sets the coordinator reference to handle rate updates and status.
     *
     * @param coordinator the coordinator instance
     */
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

     /**
     * Disconnects from the TCP platform and closes all streams.
     *
     * @param platformName the platform identifier
     * @param username     the login username
     * @param password     the login password
     */
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

    /**
     * Subscribes to a rate by sending a subscription request to the server.
     *
     * @param platformName the platform identifier
     * @param rateName     the name of the rate to subscribe to
     */
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

    /**
     * Unsubscribes from a rate by sending an unsubscribe request to the server.
     *
     * @param platformName the platform identifier
     * @param rateName     the name of the rate to unsubscribe from
     */
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

    /**
     * Main loop to listen for incoming rate updates and notify the coordinator.
     */
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
