package com.toyotabackend.mainplatform.Client;

import com.toyotabackend.mainplatform.Data_Provider.DataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class TCPSubscriber implements DataProvider { //TCP
    @Value("${client.TCP.serverAddress}")
    private String serverAddress;
    @Value("${client.TCP.port}")
    private int serverPort;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public TCPSubscriber() throws IOException {
        this.socket = new Socket(serverAddress,serverPort);
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public Boolean connect(String platformName, String username, String password) throws IOException {
        if(!platformName.equals("PF1")){
            System.out.println("(-)|Invalid platform name");
        }
        String loginMessage = String.format("login|%s|%s",username,password);
        output.println(loginMessage);
        String response = input.readLine();
        if(!response.startsWith("(+)")){
            System.out.println(response);
            return false;
        }else{
            System.out.println(response);
            return true;
        }
    }

    @Override
    public Boolean disConnect(String platformName, String username, String password) {
        if(!platformName.equals("PF1")){
            System.out.println("(-)|Invalid platform name");
        }
        if(socket != null && !socket.isClosed()) {
            try{
                socket.close();
                System.out.println("(+)|Connection closed successfully");
                return true;
            }catch(IOException e){
                System.out.println("(!)|Error closing connection");
                return false;
            }
        }
        return false;
    }

    @Override
    public String subscribe(String platformName, String rateName){
        if(!platformName.equals("PF1")){
            System.out.println("(-)|Invalid platform name");
            return platformName;
        }

        String request = String.format("subscribe|%s_%s",platformName,rateName);
        output.println(request);
        try{
            String response = input.readLine();
            System.out.println(response);
        }catch(IOException e){
            System.out.println("(!)|Cannot subscribe to rate");
        }
        return platformName;
    }

    @Override
    public void unSubscribe(String platformName, String rateName) {
        if (!platformName.equals("PF1")) {
            System.out.println("(-)|Invalid platform name");
            return;
        }

        String request = String.format("unsubscribe|%s_%s",platformName,rateName);
        output.println(request);
        try{
            String response = input.readLine();
            System.out.println(response);
        }catch(IOException e){
            System.out.println("(!)|Cannot unsubscribe to rate");
        }
    }
}
