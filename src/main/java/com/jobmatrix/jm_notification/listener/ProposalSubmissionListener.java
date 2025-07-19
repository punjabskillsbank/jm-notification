package com.jobmatrix.jm_notification.listener;

import com.common.entity.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmatrix.jm_notification.event.ProposalSubmittedEvent;
import com.jobmatrix.jm_notification.event.SnsEnvelope;
import com.jobmatrix.jm_notification.repository.ClientRepository;
import com.jobmatrix.jm_notification.service.EmailService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProposalSubmissionListener {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ClientRepository clientRepo;

    ObjectMapper objectMapper = new ObjectMapper();

    @SqsListener("testQueue-jm")
    public void handleProposalSubmitted(String messageJson) {
        try {

            log.info("Entering handleProposalSubmitted with message: {}", messageJson);

            SnsEnvelope envelope = objectMapper.readValue(messageJson, SnsEnvelope.class);

            // Step 2: Extract and deserialize the actual event
            ProposalSubmittedEvent event = objectMapper.readValue(envelope.getMessage(), ProposalSubmittedEvent.class);

            Client client = clientRepo.findEmailByClientId(event.getClientId());  //Client Entity do not have a email address, user have

            String emailContent = buildEmailBody(event); // use handlebars or plain template
            System.out.println(emailContent);

            emailService.sendEmail("pawanpreetsingh193@gmail.com", "New Proposal Received", emailContent);

            log.info("Email sent to client {}", client);
        } catch (Exception ex) {
            log.error("Failed to process proposal submission", ex);
            // Optional: Push to DLQ
        }
    }

    private String buildEmailBody(ProposalSubmittedEvent event) {
        return "<h1>You have received a new proposal!</h1>" +
                "<p>Job ID: " + event.getJobId() + "</p>" +
                "<p>Proposal ID: " + event.getProposalId() + "</p>" +
                "<p>Timestamp: " + event.getTimestamp() + "</p>" +
                "<p>Freelancer ID: " + event.getFreelancerId() + "</p>";
    }
}

