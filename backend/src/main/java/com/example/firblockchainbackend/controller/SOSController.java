package com.example.firblockchainbackend.controller;

import com.example.firblockchainbackend.model.SOSMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class SOSController {

    @MessageMapping("/sos-trigger")
    @SendTo("/topic/sos-alerts")
    public SOSMessage broadcastSOS(SOSMessage message) {
        message.setTimestamp(LocalDateTime.now().toString());
        System.out.println("Emergency SOS Received from: " + message.getSenderEmail() + " at " + message.getLatitude() + "," + message.getLongitude());
        return message;
    }
}
