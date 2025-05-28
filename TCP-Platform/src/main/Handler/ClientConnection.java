package main.Handler;

import main.Auth.LoginHandler;
import main.Rate.Rate;
import main.User.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientConnection extends Thread {
    private final Socket clientSocket;
    private final List<Rate> rateList;
    private final List<Rate> subscribeRateList;

    private final BufferedReader in;
    private final PrintWriter out;
    private final Thread rateSender;
    private final User adminUser;

    public Socket getClientSocket() { return clientSocket; }

    public ClientConnection(Socket clientSocket, List<Rate> rateList) throws IOException {
        this.adminUser = new User("admin","12345");
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

    public void Sub(String rateName){
        for(Rate rate : rateList){
            if(rate.getRateName().equals(rateName)){
                synchronized (subscribeRateList){
                    subscribeRateList.add(rate);
                }
            }
        }
    }

    public void unSub(String rateName){
        for(Rate rate : rateList){
            if(rate.getRateName().equals(rateName)){
                synchronized (subscribeRateList){
                    subscribeRateList.remove(rate);
                }
            }
        }
    }

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

    @Override
    public void run(){
        rateSender.start();
        String response;
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
