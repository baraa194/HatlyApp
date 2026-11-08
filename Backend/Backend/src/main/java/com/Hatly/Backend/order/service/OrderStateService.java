package com.Hatly.Backend.order.service;

import com.Hatly.Backend.order.dto.OrderStatusChangedEvent;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderStateService {

    private final OrderRepo  orderRepo;
    private final OrderStatusStreamPublisher streamPublisher;
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.ofEntries(
            Map.entry(OrderStatus.PENDING, Set.of(
                    OrderStatus.PAYMENT_PROCESSING,
                    OrderStatus.CANCELLED
            )),
            Map.entry(OrderStatus.PAYMENT_PROCESSING, Set.of(
                    OrderStatus.PREPARING,
                    OrderStatus.PAYMENT_FAILED,
                    OrderStatus.CANCELLED
            )),
            Map.entry(OrderStatus.PREPARING, Set.of(
                    OrderStatus.READY_FOR_PICKUP,
                    OrderStatus.CANCELLED
            )),
            Map.entry(OrderStatus.READY_FOR_PICKUP, Set.of(
                    OrderStatus.ASSIGNED,
                    OrderStatus.CANCELLED
            )),
            Map.entry(OrderStatus.ASSIGNED, Set.of(
                    OrderStatus.PICKED_UP,
                    OrderStatus.CANCELLED
            )),
            Map.entry(OrderStatus.PICKED_UP, Set.of(
                    OrderStatus.ON_THE_WAY
            )),
            Map.entry(OrderStatus.ON_THE_WAY, Set.of(
                    OrderStatus.DELIVERED
            )),
            Map.entry(OrderStatus.PAYMENT_FAILED, Set.of(
                    OrderStatus.CANCELLED,
                    OrderStatus.PAYMENT_PROCESSING
            ))

    );


    public Order transition(Long orderId, OrderStatus newStatus) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();


        if (!isTransitionAllowed(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Invalid transition from " + currentStatus + " to " + newStatus
            );
        }


        order.setStatus(newStatus);

        Order savedOrder = orderRepo.save(order);

        streamPublisher.publish(orderId, currentStatus, newStatus);
        return savedOrder;
    }


    private boolean isTransitionAllowed(OrderStatus current, OrderStatus next) {
        if (current== null) {
            return next == OrderStatus.PENDING;
        }
        return ALLOWED_TRANSITIONS
                .getOrDefault(current, Set.of())
                .contains(next);
    }
}
