package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    private String serverAddress;
    private int port;

    public ClientHandler(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    public void sendRequest(String currencyRate){
        try(
            Socket socket = new Socket(serverAddress,port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
        ){
            output.println("subscribe|PF1_"+currencyRate);

            String response = input.readLine();
            System.out.println(response);
        }catch(IOException error){
            error.printStackTrace();
        }
    }

}
