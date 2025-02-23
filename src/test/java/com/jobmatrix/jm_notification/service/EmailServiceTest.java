package com.jobmatrix.jm_notification.service;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmailService emailService;

    private final Long userId = 1L;
    private final String toEmail = "test@example.com";
    private final String username = "JohnDoe";
    private final String userType = "client";

    @BeforeEach
    void setUp() {
        // No need for openMocks() since we're using @ExtendWith(MockitoExtension.class)
    }

    /**
     * ✅ Test that a welcome email is sent and a notification is saved.
     */
    @Test
    void testSendWelcomeEmail_Success() {
        // Act
        emailService.sendWelcomeEmail(userId, toEmail, username, userType);

        // Assert - Verify email is sent
        ArgumentCaptor<SendEmailRequest> emailCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(amazonSimpleEmailService).sendEmail(emailCaptor.capture());

        SendEmailRequest sentEmail = emailCaptor.getValue();
        assertEquals(toEmail, sentEmail.getDestination().getToAddresses().get(0));
        assertTrue(sentEmail.getMessage().getSubject().getData().contains("Welcome to JobMatrix, " + username));
        assertTrue(sentEmail.getMessage().getBody().getText().getData().contains("Hello " + username));

        // Assert - Verify notification is saved
        verify(notificationService, times(1)).saveNotification(userId, "email", "welcome");
    }

    /**
     * ❌ Test when email sending fails (Amazon SES Exception)
     */
    @Test
    void testSendWelcomeEmail_EmailSendingFails() {
        // Mock SES failure
        doThrow(new RuntimeException("AWS SES Error")).when(amazonSimpleEmailService).sendEmail(any(SendEmailRequest.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendWelcomeEmail(userId, toEmail, username, userType);
        });

        assertEquals("AWS SES Error", exception.getMessage());

        // Verify notification is NOT saved when email fails
        verify(notificationService, never()).saveNotification(anyLong(), anyString(), anyString());
    }

    /**
     * ❌ Test for unexpected userType (should return generic message)
     */
    @Test
    void testSendWelcomeEmail_InvalidUserType() {
        emailService.sendWelcomeEmail(userId, toEmail, username, "randomType");

        ArgumentCaptor<SendEmailRequest> emailCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(amazonSimpleEmailService).sendEmail(emailCaptor.capture());

        SendEmailRequest sentEmail = emailCaptor.getValue();
        assertTrue(sentEmail.getMessage().getBody().getText().getData().contains("Welcome to JobMatrix!"));
    }

    /**
     * ⏳ Test performance - Should complete within 2 seconds
     */
    @Test
    void testSendWelcomeEmail_Performance() {
        assertTimeout(Duration.ofSeconds(2), () -> {
            emailService.sendWelcomeEmail(userId, toEmail, username, userType);
        });
    }
}
