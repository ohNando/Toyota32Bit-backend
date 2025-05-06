package com.toyotabackend.mainplatform.Client.Subscribers;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;
import com.toyotabackend.mainplatform.Coordinator.Coordinator;
import com.toyotabackend.mainplatform.Coordinator.CoordinatorInterface;
import com.toyotabackend.mainplatform.Dto.RateDto;
import com.toyotabackend.mainplatform.Dto.RateStatus;
import com.toyotabackend.mainplatform.Parser.ParseRateTCP;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TCPSubscriber extends Thread implements SubscriberInterface { //Subscriber 2
    
    private final CoordinatorInterface coordinator;
    @Value("${client.TCP.serverAddress}")
    private String serverAddress;
    @Value("${client.TCP.port}")
    private int serverPort;

    private List<String> subscribedRates;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    private final Logger logger = LogManager.getLogger("SubscriberLogger");

    private final Map<String, Boolean> runningFlags = new ConcurrentHashMap<>();
    private final ParseRateTCP parseRateTCP = new ParseRateTCP();

    public TCPSubscriber(String subscriberName) throws IOException {
        logger.info("Initializing" +subscriberName + "Subscriber");
        this.subscribedRates = new ArrayList<>();
        logger.info(subscriberName + "Subscriber initialized!");
    }

    public void setCoordinator(CoordinatorInterface coordinator){
        this.coordinator = coordinator;
    }

    @Override
    public Boolean connect(String platformName, String username, String password) throws IOException {
        if (!platformName.equals("PF1")) {
            System.out.println("(-)|Invalid platform name");
            return false;
        }
        socket = new Socket(serverAddress, serverPort);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String loginMessage = String.format("login|%s|%s", username, password);
        output.println(loginMessage);
        String response = input.readLine();
        if (!response.startsWith("(+)")) {
            System.out.println(response);
            return false;
        } else {
            System.out.println(response);
            return true;
        }
    }

    @Override
    public Boolean disConnect(String platformName, String username, String password) {
        if (!platformName.equals("PF1")) {
            System.out.println("(-)|Invalid platform name");
            return false;
        }
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("(+)|Connection closed successfully");
                return true;
            } catch (IOException e) {
                System.out.println("(!)|Error closing connection");
                return false;
            }
        }
        return false;
    }

    @Override
    public void subscribe(String platformName, String rateName) {
        if (!platformName.equals("PF1")) {
            System.out.println("(-)|Invalid platform name");
            return;
        }

        // Key dinamik olarak platformName ve rateName'den oluşturuluyor
        String key = platformName + "_" + rateName;
        String request = String.format("subscribe|%s_%s", platformName, rateName);
        output.println(request);
        runningFlags.put(key, true);

        // Thread'i başlatıyoruz
        this.start();
    }

    @Override
    public void unSubscribe(String platformName, String rateName) {
        if (!platformName.equals("PF1")) {
            System.out.println("(-)|Invalid platform name");
            return;
        }
        // Key dinamik olarak platformName ve rateName'den oluşturuluyor
        String key = platformName + "_" + rateName;
        runningFlags.put(key, false);
        String request = String.format("unsubscribe|%s_%s", platformName, rateName);
        output.println(request);

        try {
            String response = input.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println("(!)|Cannot unsubscribe from rate");
        }
    }

    @Override
    public void run() {
        try {
            // Bu key, subscribe metodunda dinamik olarak oluşturuluyor
            String key = Thread.currentThread().getName();  // Thread ismini key olarak kullanabiliriz
            RateStatus status = RateStatus.AVAILABLE;
            String[] parts = key.split("_");
            String platformName = parts[0];
            String rateName = parts[1];
            while (runningFlags.get(key)) {
                String response = input.readLine();
                if (response == null || response.isEmpty()) continue;  // Boş veya null veri geldiğinde atla
                if (response.startsWith("(-)")) continue;  // Hata mesajı geldiğinde atla

                // Veriyi parse et
                RateDto dto = parseRateTCP.parseRate(response);
                if (dto == null) continue;  // Eğer veri geçerli değilse atla

                // İlk veri geldiğinde 'available' durumunu bildir
                if (status == status.AVAILABLE) {
                    coordinator.onRateAvailable(key.split("_")[0], key.split("_")[1], dto);
                    status = status.UNAVAILABLE;  // Durumu değiştirme
                    coordinator.onRateStatus(platformName, rateName, status);
                } else {
                    coordinator.onRateUpdate(key.split("_")[0], key.split("_")[1], new RateFields(dto.getBid(), dto.getAsk(), dto.getRateUpdateTime()));
                    coordinator.onRateStatus(platformName, rateName, status);
                }
            }
        } catch (IOException e) {
            System.out.println("(!)|Error while reading data: " + e.getMessage());
        }
    }
}
