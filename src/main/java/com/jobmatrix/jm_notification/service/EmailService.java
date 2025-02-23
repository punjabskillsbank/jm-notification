package com.jobmatrix.jm_notification.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final NotificationService notificationService;

    @Value("${aws.ses.sender-email}")
    private String senderEmail;

    public EmailService(AmazonSimpleEmailService amazonSimpleEmailService, NotificationService notificationService) {
        this.amazonSimpleEmailService = amazonSimpleEmailService;
        this.notificationService = notificationService;
    }

    public void sendWelcomeEmail(Long userId, String toEmail, String username, String userType) { // ADDED username
        String subject = "Welcome to JobMatrix, " + username + "! 🎉"; // INCLUDE USERNAME IN SUBJECT
        String body = generateWelcomeMessage(username, userType); // PASS username

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(toEmail))
                .withMessage(new Message()
                        .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(body)))
                        .withSubject(new Content().withCharset("UTF-8").withData(subject)))
                .withSource(senderEmail);

        amazonSimpleEmailService.sendEmail(request);

        // Save email notification to the database
        notificationService.saveNotification(userId, "email", "welcome");
    }

    private String generateWelcomeMessage(String username, String userType) { // ADDED username
        if ("client".equalsIgnoreCase(userType)) {
            return "Hello " + username + ",\n\nWelcome to JobMatrix! We are excited to have you as a client. 😊";
        } else if ("freelancer".equalsIgnoreCase(userType)) {
            return "Hello " + username + ",\n\nWelcome to JobMatrix! We are excited to have you as a freelancer. 🚀";
        } else {
            return "Hello " + username + ",\n\nWelcome to JobMatrix!";
        }
    }
}
