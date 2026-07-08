package com.Hatly.Backend.deliveryAgent.controller;

import com.Hatly.Backend.deliveryAgent.dto.*;
import com.Hatly.Backend.deliveryAgent.service.DeliveryAgentService;
import com.Hatly.Backend.order.dto.OrderDeliveryResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-agents")
public class DeliveryAgentController {

    @Autowired
    private DeliveryAgentService deliveryAgentService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<AgentInfoResponse> getAgentInfoByUserId(@PathVariable Long userId) {
        AgentInfoResponse response = deliveryAgentService.getAgentInfoByUserId(userId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{agentId}/status")
    public ResponseEntity<UpdateAgentStatusResponse> updateAgentStatus(
            @PathVariable Long agentId,
            @RequestParam Boolean isOnline) {

        UpdateAgentStatusResponse response = deliveryAgentService.updateAgentStatus(agentId, isOnline);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{agentId}/location")
    public ResponseEntity<String> updateAgentLocation(
            @PathVariable Long agentId,
            @Valid @RequestBody UpdateLocationRequest request) {

        deliveryAgentService.updateAgentLocation(agentId, request);
        return ResponseEntity.ok("Location updated successfully.");
    }


    @PostMapping("/{agentId}/orders/{orderId}/action")
    public ResponseEntity<String> handleOrderAction(
            @PathVariable Long agentId,
            @PathVariable Long orderId,
            @Valid @RequestBody AgentOrderActionRequest request) {

        deliveryAgentService.handleOrderAction(orderId, agentId, request);
        return ResponseEntity.ok("Order action '" + request.getAction() + "' processed successfully.");
    }


    @PutMapping("/{agentId}/orders/{orderId}/delivery-status")
    public ResponseEntity<String> updateDeliveryStatus(
            @PathVariable Long agentId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request) {

        deliveryAgentService.updateDeliveryStatus(orderId, agentId, request);
        return ResponseEntity.ok("Delivery status updated to: " + request.getStatus());
    }

}
