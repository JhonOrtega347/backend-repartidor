package com.example.deliverytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSocketMessageBroker
public class DeliveryTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryTrackerApplication.class, args);
    }

}
