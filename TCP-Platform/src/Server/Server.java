package Server;

import Handler.ClientConnection;
import Rate.Rate;
import Config.RateConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multithreaded server that listens for client connections,
 * manages connected clients, and runs rate update threads.
 * <p>
 * The server maintains a list of active clients and rates, and handles
 * client acceptance and disconnection monitoring in separate threads.
 * </p>
 */
public class Server extends Thread {
    private boolean serverStatus = true;
    private final ServerSocket serverSocket;
    private final Thread disconnectThread;


    private final List<ClientConnection> clientList;
    private final List<Rate> rateList;

    public boolean getServerStatus() { return this.serverStatus; }

    /**
     * Constructs a new Server instance.
     * <p>
     * Initializes the ServerSocket, client and rate lists,
     * creates Rate instances based on configuration,
     * and starts the rate threads.
     * </p>
     *
     * @throws IOException if the server socket fails to open
     */
    public Server() throws IOException {

        this.disconnectThread = new Thread( () -> {
           try{
               this.closeDisconnectThread();
           }catch (Exception e){
               throw new RuntimeException(e);
           }
        });
        this.serverSocket = new ServerSocket(8081);
        this.clientList = new ArrayList<>();
        this.rateList = new ArrayList<>();

        // Initialize Rate threads based on configuration
        for(String rateName : RateConfig.getRateNames()){
            rateList.add(new Rate(this, rateName,
                    RateConfig.getRateBid(rateName), RateConfig.getRateAsk(rateName)));
        }
        // Start each Rate thread
        for(Rate rate : rateList){
            rate.start();
            System.out.println("Rate started at " + rate.getRateName());
        }
        this.serverStatus = true;
    }

    /**
     * Main execution method for the server thread.
     * <p>
     * Starts the disconnect monitoring thread, listens for client connections,
     * accepts clients, and handles server shutdown when stopped.
     * </p>
     */
    @Override
    public void run(){
        disconnectThread.start();
        System.out.println("Server started...");

        do{
            try {
                this.acceptClients(serverSocket.accept());
                System.out.println("Client accepted");
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }while(serverStatus);
        System.out.println("Server about to close...");

        try{
            closeAllClient();
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        try{
            this.serverSocket.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        this.stopServer();
        System.out.println("Server closed");
    }

    public void stopServer(){
        this.serverStatus = false;
    }

    public void closeAllClient() throws IOException {
        for(ClientConnection clientConn : this.clientList){
            clientConn.getClientSocket().close();
            this.clientList.remove(clientConn);
        }
    }

    public void closeClientConn(ClientConnection clientConn) throws IOException {
        clientConn.getClientSocket().close();
    }

    public void acceptClients(Socket socket) throws IOException{
        clientList.add(new ClientConnection(socket,rateList));
    }

    public void closeDisconnectThread() throws InterruptedException, IOException {
        do {
            for(ClientConnection clientConn : this.clientList){
                if(!clientConn.getClientSocket().isConnected()){
                    closeClientConn(clientConn);
                    this.clientList.remove(clientConn);
                }
            }
            Thread.sleep(2000);
        }while(serverStatus);
    }
}
