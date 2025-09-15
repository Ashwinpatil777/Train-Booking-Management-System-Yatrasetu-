package com.irctc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Base64;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${spring.mail.host}")
    private String smtpHost;
    
    @Value("${spring.mail.port}")
    private int smtpPort;

    public void sendTicketEmail(String to, String pnr, String ticketImage) throws MessagingException, IOException {
        System.out.println("\n===== Sending Email =====");
        System.out.println("From: " + fromEmail);
        System.out.println("To: " + to);
        System.out.println("SMTP Server: " + smtpHost + ":" + smtpPort);
        System.out.println("PNR: " + pnr);
        System.out.println("Ticket Image Present: " + (ticketImage != null && !ticketImage.isEmpty()));
        
        try {
            // Validate input parameters
            if (to == null || to.isBlank()) {
                throw new IllegalArgumentException("Recipient email cannot be empty");
            }
            if (pnr == null || pnr.isBlank()) {
                throw new IllegalArgumentException("PNR cannot be empty");
            }

            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(fromEmail, "Yatrasetu Support"));
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setTo(to.trim());
            helper.setSubject("Your Booking Confirmation for PNR: " + pnr);

            // Create email content
            String htmlContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                    "<h2 style='color: #1a237e;'>Booking Confirmation</h2>" +
                    "<p>Dear Valued Customer,</p>" +
                    "<p>Thank you for booking with Yatrasetu. Your e-ticket is attached to this email.</p>" +
                    "<p><strong>PNR:</strong> " + pnr + "</p>" +
                    "<p>Please find your ticket attached. You can also download it from your account.</p>" +
                    "<br>" +
                    "<p>Safe travels!</p>" +
                    "<p><strong>The Yatrasetu Team</strong></p>" +
                    "</div>";

            helper.setText(htmlContent, true);

            // Handle ticket image attachment if provided
            if (ticketImage != null && !ticketImage.isBlank()) {
                try {
                    // Handle different base64 formats (with or without data:image prefix)
                    String base64Image = ticketImage;
                    if (ticketImage.contains(",")) {
                        base64Image = ticketImage.split(",")[1];
                    }
                    
                    // Decode the base64 image
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image.trim());
                    
                    // Add the image as an attachment
                    helper.addAttachment("ticket-" + pnr + ".png", 
                                      new ByteArrayResource(imageBytes), 
                                      "image/png");
                } catch (Exception e) {
                    System.err.println("Error attaching ticket image: " + e.getMessage());
                    // Continue without the attachment rather than failing the entire email
                }
            }

            // Send the email
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
            
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
