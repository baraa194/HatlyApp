package com.Hatly.Backend.order.repo;

import com.Hatly.Backend.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  OrderItemRepo extends JpaRepository<OrderItem,Long> {
}
