package com.Hatly.Backend.deliveryAgent.dto;

import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UpdateAgentStatusResponse {
    private Boolean isOnline;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;
}
