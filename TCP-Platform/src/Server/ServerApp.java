package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ServerApp {
    public static void main(String[] args) {
        try{
            int clientCount = 0;
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/config.properties"));
            int port = Integer.parseInt(properties.getProperty("tcp-platform.server.port"));
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerHandler(socket,properties)).start();
                System.out.printf("Client %d connected\n",++clientCount);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
