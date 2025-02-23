package com.jobmatrix.jm_notification.controller;

import com.jobmatrix.jm_notification.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/client")
    public ResponseEntity<String> sendClientWelcomeEmail(
            @RequestParam Long userId,
            @RequestParam String toEmail,
            @RequestParam String username) { // NEW PARAMETER

        emailService.sendWelcomeEmail(userId, toEmail, username, "client"); // PASSING username
        return ResponseEntity.ok("Client welcome email sent successfully to " + toEmail);
    }

    @PostMapping("/freelancer")
    public ResponseEntity<String> sendFreelancerWelcomeEmail(
            @RequestParam Long userId,
            @RequestParam String toEmail,
            @RequestParam String username) { // NEW PARAMETER

        emailService.sendWelcomeEmail(userId, toEmail, username, "freelancer"); // PASSING username
        return ResponseEntity.ok("Freelancer welcome email sent successfully to " + toEmail);
    }
}
