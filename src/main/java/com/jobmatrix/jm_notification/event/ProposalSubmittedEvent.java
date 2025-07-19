package com.jobmatrix.jm_notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProposalSubmittedEvent {
    private Long proposalId;
    private Long jobId;
    private UUID clientId;
    private UUID freelancerId;
    private String timestamp;
}

