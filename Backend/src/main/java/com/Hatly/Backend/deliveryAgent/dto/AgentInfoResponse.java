package com.Hatly.Backend.deliveryAgent.dto;

import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import com.Hatly.Backend.user.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AgentInfoResponse {

    private Long user_id;

    private String vehicleType;

    private String vehicleNumber;

    private Boolean isOnline;


    private String status;

    private LocalDateTime joinedAt;
}
