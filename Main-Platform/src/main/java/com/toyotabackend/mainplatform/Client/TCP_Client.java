package com.toyotabackend.mainplatform.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Component
public class TCP_Client {
    @Value("${client.TCP.serverAddress}")
    private String serverAddress;
    @Value("${client.TCP.port}")
    private int serverPort;

    public String sendRequest(String request) {
        StringBuilder builder = new StringBuilder();
        try(Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            output.println(request);
            String response;
            while((response = input.readLine()) != null) {
                builder.append(response).append("\n");
            }
        }catch (IOException error){
            return "(-)|ERROR :" + error.getMessage();
        }
        return builder.toString();
    }
}
