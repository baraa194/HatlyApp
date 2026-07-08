package com.Hatly.Backend.deliveryAgent.model;

import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import com.Hatly.Backend.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_agents")
@Data
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String vehicleType;

    private String vehicleNumber;

    private Boolean isOnline;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    private LocalDateTime joinedAt;
    @OneToOne(mappedBy = "agent")
    private AgentPresence agentPresence;
}
