package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ServerApp {
    public static void main(String[] args) {
        Properties properties = new Properties();
        String configFilePath = System.getProperty("user.dir") + "/src/config.properties";
        try(FileInputStream configFile = new FileInputStream(configFilePath)){
            properties.load(configFile);
            int port = Integer.parseInt(properties.getProperty("tcp-platform.server.port"));

            try(ServerSocket serverSocket = new ServerSocket(port)){
                System.out.println("Server started on port " + port);
                short clientCount = 0;
                while(true){
                    try{
                        Socket socket = serverSocket.accept();
                        new Thread(new ServerHandler(socket,properties)).start();
                        System.out.printf("Client %d connected\n",++clientCount);
                    }catch (IOException error){
                        System.out.println("(!)|client cannot connected" + error.getMessage());
                        break;
                    }
                }
            }
        }catch(IOException error){
            System.out.println("(!)|server cannot started" + error.getMessage());
            error.printStackTrace();
        }
    }
}
