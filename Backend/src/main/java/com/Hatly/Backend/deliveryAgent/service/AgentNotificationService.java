package com.Hatly.Backend.deliveryAgent.service;

import com.Hatly.Backend.order.model.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AgentNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public AgentNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void sendOrderOffer(Long agentId, Order order) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", order.getId());
        orderData.put("restaurantName", order.getRestaurant().getName());
        orderData.put("branch", order.getBranch().getAddressDetailed());
        orderData.put("customerName", order.getCustomer().getName());
        orderData.put("customerAddress", order.getCustomerAddress().getCity());
        orderData.put("lng",order.getDeliveryLng());
        orderData.put("lat",order.getDeliveryLat());
        orderData.put("totalAmount", order.getTotal());
        orderData.put("message", "New delivery job available nearby!");


        String destination = "/queue/agent/" + agentId + "/orders";


        messagingTemplate.convertAndSend(destination, orderData);
        System.out.println("Order #" + order.getId() + " sent via WebSocket to Agent #" + agentId);
    }
}
