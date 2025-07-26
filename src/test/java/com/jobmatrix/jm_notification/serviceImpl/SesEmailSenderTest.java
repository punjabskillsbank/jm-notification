package com.jobmatrix.jm_notification.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SesEmailSenderTest {

    @Mock
    private SesClient sesClient;

    @InjectMocks
    private SesEmailSender sesEmailSender;

    @BeforeEach
    void setUp() {
        // Inject dummy sender email using reflection since it's @Value-injected
        ReflectionTestUtils.setField(sesEmailSender, "sender", "noreply@jobmatrix.com");
    }

    @Test
    void shouldCallSesClientWithCorrectRequest() {
        // Given
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "<p>This is a test email.</p>";

        // When
        sesEmailSender.sendEmail(to, subject, body);

        // Then
        ArgumentCaptor<SendEmailRequest> captor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient).sendEmail(captor.capture());

        SendEmailRequest request = captor.getValue();

        assertEquals("noreply@jobmatrix.com", request.source());
        assertTrue(request.destination().toAddresses().contains(to));
        assertEquals(subject, request.message().subject().data());
        assertEquals(body, request.message().body().html().data());
    }
}
