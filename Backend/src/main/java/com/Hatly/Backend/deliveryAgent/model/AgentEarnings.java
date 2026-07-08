package com.Hatly.Backend.deliveryAgent.model;

import com.Hatly.Backend.order.model.Order;
import jakarta.persistence.*;

@Entity
@Table(name = "agent_earnings")
public class AgentEarnings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private DeliveryAgent agent;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private Long amount;
}
