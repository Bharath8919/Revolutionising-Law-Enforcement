package com.example.firblockchainbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@firblockchain.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendStatusUpdateEmail(com.example.firblockchainbackend.model.FIR fir) {
        if (fir.getFiledBy() != null && fir.getFiledBy().getEmail() != null) {
            String subject = "Case Update: FIR #" + fir.getId() + " Status Changed";
            String body = "Dear " + fir.getFiledBy().getUsername() + ",\n\n" +
                          "Your reported case (ID: #" + fir.getId() + ") has been updated to status: " + fir.getStatus() + ".\n\n" +
                          "Description: " + fir.getDescription() + "\n\n" +
                          "Please log in to the Decentralized FIR Portal to view full details and investigation notes.\n\n" +
                          "Regards,\nLaw Enforcement Department";
            
            sendEmail(fir.getFiledBy().getEmail(), subject, body);
            System.out.println("✅ Status update email sent to: " + fir.getFiledBy().getEmail());
        }
    }
}
