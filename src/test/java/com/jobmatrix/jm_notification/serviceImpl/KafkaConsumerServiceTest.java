package com.jobmatrix.jm_notification.serviceImpl;

import com.common.entity.User;
import com.common.event.ProposalSubmittedEvent;
import com.jobmatrix.jm_notification.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private SesEmailSender sesEmailSender;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void shouldSendEmailToClientWhenProposalEventIsReceived() {
        // Given
        UUID clientId = UUID.randomUUID();
        ProposalSubmittedEvent event = ProposalSubmittedEvent.builder()
                .proposalId(1L)
                .jobPostingId(101)
                .clientId(clientId)
                .freelancerId(UUID.randomUUID())
                .build();

        User user = User.builder()
                .userId(clientId)
                .email("client@example.com")
                .build();

        Mockito.when(userAccountRepository.findById(clientId)).thenReturn(Optional.of(user));

        // When
        kafkaConsumerService.consume(event, acknowledgment);

        // Then
        verify(sesEmailSender).sendEmail(
                eq("client@example.com"),
                eq("New Proposal Received"),
                contains("JobId: 101")
        );

    }
    @Test
    void shouldThrowUserNotFoundExceptionWhenClientDoesNotExist() {
        // Given
        UUID clientId = UUID.randomUUID();
        ProposalSubmittedEvent event = ProposalSubmittedEvent.builder()
                .proposalId(1L)
                .jobPostingId(101)
                .clientId(clientId)
                .freelancerId(UUID.randomUUID())
                .build();

        Mockito.when(userAccountRepository.findById(clientId)).thenReturn(Optional.empty());

        kafkaConsumerService.consume(event, acknowledgment);

        verify(sesEmailSender, never()).sendEmail(any(), any(), any());

    }

    @Test
    void shouldLogErrorWhenEmailSendingFails() {
        // Given
        UUID clientId = UUID.randomUUID();
        ProposalSubmittedEvent event = ProposalSubmittedEvent.builder()
                .proposalId(1L)
                .jobPostingId(101)
                .clientId(clientId)
                .freelancerId(UUID.randomUUID())
                .build();

        User user = User.builder()
                .userId(clientId)
                .email("client@example.com")
                .build();

        Mockito.when(userAccountRepository.findById(clientId)).thenReturn(Optional.of(user));
        Mockito.doThrow(new RuntimeException("SES error"))
                .when(sesEmailSender).sendEmail(any(), any(), any());

        // When
        kafkaConsumerService.consume(event, acknowledgment);

        // Then
        verify(sesEmailSender).sendEmail(any(), any(), any());

    }
}