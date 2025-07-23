package com.jobmatrix.jm_notification.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
public class SesEmailSender {

    @Value("${ses.sender.email}")
    private String sender;

    private final SesClient sesClient;

    public void sendEmail(String to, String subject, String htmlBody) {
        SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(to).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder()
                                .html(Content.builder().data(htmlBody).build())
                                .build())
                        .build())
                .source(sender)
                .build();

        sesClient.sendEmail(request);
    }
}
