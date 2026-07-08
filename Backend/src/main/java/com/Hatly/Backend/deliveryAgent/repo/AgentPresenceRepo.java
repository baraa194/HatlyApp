package com.Hatly.Backend.deliveryAgent.repo;

import com.Hatly.Backend.deliveryAgent.model.AgentPresence;
import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentPresenceRepo extends JpaRepository<AgentPresence,Long> {
    Optional<AgentPresence> findByAgentId(Long Id);
    Optional<AgentPresence> findByAgent(DeliveryAgent agent);





}

