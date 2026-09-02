package com.Hatly.Backend.order.service;

import com.Hatly.Backend.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            String orderIdStr = message.getValue().get("orderId");
            String newStatus = message.getValue().get("newStatus");
            String oldStatus = message.getValue().get("oldStatus");

            Long orderId = Long.parseLong(orderIdStr);

            log.info("Order status changed - orderId: {}, from: {} to: {}", orderId, oldStatus, newStatus);


            messagingTemplate.convertAndSend("/topic/orders/" + orderId, message.getValue());



            acknowledge(message);

        } catch (Exception e) {
            log.error("Error processing OrderStatusChangedEvent", e);
        }
    }

    private void acknowledge(MapRecord<String, String, String> message) {
        stringRedisTemplate.opsForStream()
                .acknowledge("order-status-stream", "order-group", message.getId());
    }
}