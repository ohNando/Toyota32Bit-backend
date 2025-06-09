package Handler;

import Auth.LoginHandler;
import Rate.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client connection on the server side.
 * <p>
 * Handles authentication, subscription management to rates,
 * and sends subscribed rate updates periodically to the client.
 * </p>
 * <p>
 * This class extends Thread and manages reading client requests and
 * responding accordingly.
 * </p>
 */
public class ClientConnection extends Thread {
    private final Socket clientSocket;
    private final List<Rate> rateList;
    private final List<Rate> subscribeRateList;

    private final BufferedReader in;
    private final PrintWriter out;
    private final Thread rateSender;

    /**
     * Returns the socket associated with this client connection.
     *
     * @return the client socket
     */
    public Socket getClientSocket() { return clientSocket; }

    /**
     * Constructs a new ClientConnection object.
     * Initializes the input/output streams, authenticates the client,
     * and prepares the subscription list and rate sender thread.
     *
     * @param clientSocket the socket connected to the client
     * @param rateList the list of available rates on the server
     * @throws IOException if an I/O error occurs when creating input/output streams
     */
    public ClientConnection(Socket clientSocket, List<Rate> rateList) throws IOException {
        this.rateList = rateList;
        this.subscribeRateList = new ArrayList<>();
        this.clientSocket = clientSocket;
        this.rateSender = new Thread( () -> {
            try{
                this.sendSubRates();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });

        try{ in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); }
        catch (IOException e){ throw new RuntimeException(e); }

        try{ out = new PrintWriter(clientSocket.getOutputStream(),true); }
        catch (IOException e){ throw new RuntimeException(e); }

        String connMessage = in.readLine();
        if(connMessage == null){
            this.clientSocket.close();
            return;
        }

        if(!LoginHandler.authenticate(connMessage)){
            System.err.println("Invalid username or password");
            this.clientSocket.close();
            return;
        }

    this.start();
    }

    /**
     * Sends the subscribed rates to the client every second.
     * Iterates over the subscribed rates and writes rate details
     * to the client's output stream.
     *
     * @throws InterruptedException if the sending thread is interrupted during sleep
     */
    public void sendSubRates() throws InterruptedException {
        do{
            synchronized (subscribeRateList){
                for(Rate rate: subscribeRateList){
                    out.println(rate.getRateName() + "|" + rate.getAsk() + "|" + rate.getBid() + "|" + rate.getTimestamp());
                }
            }
            Thread.sleep(1000);
        }while(clientSocket.isConnected());
    }

    /**
     * Adds the specified rate to the subscription list if available.
     *
     * @param rateName the name of the rate to subscribe
     */
    public void Sub(String rateName){
        for(Rate rate : rateList){
            if(rate.getRateName().equals(rateName)){
                synchronized (subscribeRateList){
                    subscribeRateList.add(rate);
                }
            }
        }
    }

    /**
     * Removes the specified rate from the subscription list if present.
     *
     * @param rateName the name of the rate to unsubscribe
     */
    public void unSub(String rateName){
        for(Rate rate : rateList){
            if(rate.getRateName().equals(rateName)){
                synchronized (subscribeRateList){
                    subscribeRateList.remove(rate);
                }
            }
        }
    }

    /**
     * Handles incoming client requests to subscribe or unsubscribe to rates.
     * The request format is expected to be "subscribe|rateName" or "unsubscribe|rateName".
     * Prints error messages for invalid requests.
     *
     * @param request the client request string
     */
    public void messageHandler(String request){
        String[] parts = request.split("\\|");
        if(parts.length != 2){
            System.err.println("Invalid request");
            return;
        }
        switch (parts[0]){
            case "subscribe":
                Sub(parts[1]);
                break;
            case "unsubscribe":
                unSub(parts[1]);
                break;
            default:
                System.err.println("Invalid request");
                break;
        }
    }

    /**
     * The main thread execution method.
     * Starts the rateSender thread to send subscribed rates,
     * continuously listens for incoming client messages,
     * processes them and handles client disconnection.
     */
    @Override
    public void run(){
        rateSender.start();
        do{
            try{
                messageHandler(in.readLine());
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }while(clientSocket.isConnected());

        System.out.println("Client disconnected");
        try{
            clientSocket.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
