package com.jobmatrix.jm_notification.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private AmazonSimpleEmailService sesClient;

    public void sendEmail(String to, String subject, String htmlBody) {
        log.info("Sending email to: {}, subject: {}", to, subject);
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(new Message()
                        .withSubject(new Content(subject))
                        .withBody(new Body().withHtml(new Content(htmlBody))))
                .withSource("pawanpreetsingh193@gmail.com");

        sesClient.sendEmail(request);
    }
}

