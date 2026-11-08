package com.Hatly.Backend.deliveryAgent.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_presence")
@Data
public class AgentPresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastSeenAt;

    private BigDecimal lastLat;

    private BigDecimal lastLng;
    @OneToOne
    @JoinColumn(name = "agent_id")
    private DeliveryAgent agent;
}
