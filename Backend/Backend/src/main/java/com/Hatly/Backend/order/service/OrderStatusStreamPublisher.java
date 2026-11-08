package com.Hatly.Backend.order.service;

import com.Hatly.Backend.order.dto.OrderStatusChangedEvent;
import com.Hatly.Backend.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderStatusStreamPublisher {
    private final StringRedisTemplate redisTemplate;
    private static final String STREAM_KEY = "order-status-stream";

    public void publish(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                orderId,
                oldStatus,
                newStatus,
                Instant.now()

        );
        String oldStatusName = (oldStatus != null) ? oldStatus.name() : "NONE";

        Map<String, String> message = Map.of(
                "orderId", orderId.toString(),
                "oldStatus", oldStatusName,
                "newStatus", newStatus.name(),
                "changedAt", event.getChangedAt().toString()

        );

        redisTemplate.opsForStream().add(STREAM_KEY, message);
    }
}
