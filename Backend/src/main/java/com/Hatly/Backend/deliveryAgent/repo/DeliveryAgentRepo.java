package com.Hatly.Backend.deliveryAgent.repo;

import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAgentRepo extends JpaRepository<DeliveryAgent,Long> {
    //Optional<DeliveryAgent> findByRestaurnat_branch_id(Long id);
    Optional<DeliveryAgent> findByUserId(Long id);
    List<DeliveryAgent> findAllByUserIdInAndStatusAndIsOnlineTrue(List<Long> ids, String status);
}
