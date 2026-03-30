package com.example.firblockchainbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SOSMessage {
    private String senderEmail;
    private String senderName;
    private double latitude;
    private double longitude;
    private String timestamp;
    private String message;
}
