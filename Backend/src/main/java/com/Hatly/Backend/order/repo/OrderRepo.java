package com.Hatly.Backend.order.repo;

import com.Hatly.Backend.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,Long> {
    List<Order> findByCustomerIdAndRestaurantIdOrderByCreatedAtDesc(Long userId, Long restaurantId);


    List<Order> findByBranchIdAndRestaurantIdOrderByCreatedAtDesc(Long branchId, Long restaurantId);

    Optional<Order> findById (Long Id);
}
