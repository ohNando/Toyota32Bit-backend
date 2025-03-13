package com.toyotabackend.mainplatform.TCP_Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class TCP_Client {
    @Value("${client.TCP.serverAdress}")
    private String serverAddress;
    @Value("${client.TCP.port}")
    private int serverPort;

    public String sendRequest(String request) {
        try(Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            output.println(request);
            String response;
            while((response = input.readLine()) != null) {
                System.out.println(response);
            }
            return response;
        }catch (IOException error){
            error.printStackTrace();
            return "(-)|ERROR :" + error.getMessage();
        }
    }
}
