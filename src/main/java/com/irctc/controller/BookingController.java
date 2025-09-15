package com.irctc.controller;

import com.irctc.dto.BookingResponseDTO;
import com.irctc.dto.EmailRequest;
import com.irctc.dto.SeatBookingRequest;
import com.irctc.model.Booking;
import com.irctc.service.BookingService;
import com.irctc.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final EmailService emailService;

    @PostMapping("/book")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> bookSeats(@Valid @RequestBody SeatBookingRequest request) {
        try {
            Booking booking = bookingService.bookSeats(request);
            // Convert the Booking entity to a BookingResponseDTO
            BookingResponseDTO responseDto = BookingResponseDTO.fromEntity(booking);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserBookings() {
        try {
            // This will get the currently authenticated user's bookings
            List<Booking> userBookings = bookingService.getUserBookings();
            // Convert to DTOs
            List<BookingResponseDTO> responseDtos = userBookings.stream()
                .map(BookingResponseDTO::fromEntity)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            // This will get the booking by ID
            Optional<Booking> booking = bookingService.getBookingById(id);
            if (booking.isPresent()) {
                BookingResponseDTO responseDto = BookingResponseDTO.fromEntity(booking.get());
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pnr/{pnr}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBookingByPnr(@PathVariable String pnr) {
        try {
            BookingService.PnrDetailsResponse pnrDetails = bookingService.getPnrDetails(pnr);
            return ResponseEntity.ok(pnrDetails);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cancel/{pnr}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelBooking(@PathVariable String pnr) {
        try {
            bookingService.cancelBooking(pnr);
            return ResponseEntity.ok(Map.of("message", "Booking with PNR " + pnr + " has been cancelled."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/send-email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendBookingEmail(@RequestBody EmailRequest emailRequest) {
        try {
            // Log the incoming request (without sensitive data)
            System.out.println("Sending email to: " + emailRequest.getEmail());
            System.out.println("PNR: " + emailRequest.getPnr());
            System.out.println("Ticket image present: " + 
                (emailRequest.getTicketImage() != null && !emailRequest.getTicketImage().isEmpty()));
            
            // Validate request
            if (emailRequest.getEmail() == null || emailRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (emailRequest.getPnr() == null || emailRequest.getPnr().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "PNR is required"));
            }
            
            // Send the email
            emailService.sendTicketEmail(
                emailRequest.getEmail().trim(),
                emailRequest.getPnr().trim(),
                emailRequest.getTicketImage()
            );
            
            System.out.println("Email sent successfully to: " + emailRequest.getEmail());
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
            
        } catch (MessagingException e) {
            System.err.println("Email sending failed (MessagingException): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to send email: " + e.getMessage(),
                "details", "Check SMTP configuration and credentials"
            ));
        } catch (IOException e) {
            System.err.println("IO Error while sending email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to process email content: " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Unexpected error sending email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "An unexpected error occurred: " + e.getMessage(),
                "type", e.getClass().getName()
            ));
        }
    }
}