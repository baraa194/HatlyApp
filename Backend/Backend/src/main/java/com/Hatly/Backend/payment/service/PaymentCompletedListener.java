package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.deliveryAgent.service.DeliveryAgentService;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.user.service.CustomerNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final DeliveryAgentService agentService;
    private final CustomerNotificationService customerNotificationService;
    private final OrderRepo  orderRepo;
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            String orderIdStr = message.getValue().get("orderId");
            Long orderId = Long.parseLong(orderIdStr);

            Order order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            if (order.getPaymentStatus() != PaymentStatus.PAID) {
                log.warn("Order {} already processed or not in expected state", orderId);
                acknowledge(message);
                return;
            }

            log.info("Received PaymentCompletedEvent for orderId: {}", orderId);


            customerNotificationService.notifyCustomerPaymentSuccess(orderId);
            agentService.processOrderAssignment(order);
            acknowledge(message);

        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent", e);
        }
    }
    private void acknowledge(MapRecord<String, String, String> message) {
        stringRedisTemplate.opsForStream()
                .acknowledge("payment-events", "payment-group", message.getId());
    }
}