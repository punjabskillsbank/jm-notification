package com.jobmatrix.jm_notification.serviceImpl;

import com.common.entity.User;
import com.common.event.ProposalSubmittedEvent;
import com.common.exceptionHandling.UserNotFoundException;
import com.jobmatrix.jm_notification.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SesEmailSender sesEmailSender;
    private final UserAccountRepository userAccountRepository;

    @KafkaListener(topics = "proposal-submitted", groupId = "jm-notification-group")
    public void consume(ProposalSubmittedEvent event, Acknowledgment ack) {
        try {
            log.info("Received proposal event: {}", event);

            String clientEmail = "";

            Optional<User> user = userAccountRepository.findById(event.getClientId());
            if(user.isEmpty()){
                throw new UserNotFoundException(event.getClientId());
            }
            else {
                clientEmail = user.get().getEmail();
            }

            String body = String.format(
                    "<h2>New Proposal Received</h2>" +
                    "<p>For JobId: %s</p>",
                    event.getJobPostingId() +
                    "<br><i>Regards</i><br>" +
                    "<b>JobMatrix Team</b>"
                    );
            sesEmailSender.sendEmail(clientEmail, "New Proposal Received", body);
            log.info("Email sent to client: {}", clientEmail);
        } catch (Exception e) {
            log.error("Failed to process event: {}", e.getMessage());
        }
    }
}

