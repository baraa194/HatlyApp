package com.Hatly.Backend.user.service;

import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrderRepo orderRepo;

    public void notifyCustomerPaymentSuccess(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));


        Long customerId = order.getCustomer().getId();
        String customerName = order.getCustomer().getName();


        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "PAYMENT_SUCCESS");
        payload.put("orderId", order.getId());
        payload.put("message", "Payment processed successfully, " + customerName + "! Your order is being prepared.");
        payload.put("totalAmount", order.getTotal());
        payload.put("status", order.getStatus().toString());


        payload.put("timestamp", Instant.now().toString());
        payload.put("restaurantName", order.getRestaurant().getName());
        payload.put("paymentMethod", "CARD");


        String destination = "/queue/customer/" + customerId + "/orders";



        messagingTemplate.convertAndSend(destination, payload);

        System.out.println("Payment success notification sent to Customer #" + customerId + " for Order #" + orderId);
    }
}