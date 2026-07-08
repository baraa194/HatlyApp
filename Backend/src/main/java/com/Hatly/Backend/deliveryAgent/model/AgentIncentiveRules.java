package com.Hatly.Backend.deliveryAgent.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agent_incentives_rule")
public class AgentIncentiveRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String ruleName;
    private int incentiveValue;
}
