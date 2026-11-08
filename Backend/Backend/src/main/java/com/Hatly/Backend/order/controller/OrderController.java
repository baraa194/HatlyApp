package com.Hatly.Backend.order.controller;

import com.Hatly.Backend.order.dto.OrderDeliveryResponse;
import com.Hatly.Backend.order.dto.OrderRequest;
import com.Hatly.Backend.order.dto.OrderResponse;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestParam Long userId
    ) {
        OrderResponse response = orderService.createOrder(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/calculate-fees")
    public ResponseEntity<Map<String, Object>> calculateFees(
            @Valid @RequestBody OrderRequest request,
            @RequestParam Long userId
    ) {
        Map<String, Object> fees = orderService.calculateFeesOnly(request, userId);
        return ResponseEntity.ok(fees);
    }

    @GetMapping("/restaurant/branch/{restId}/{branchId}")
    public ResponseEntity<List<OrderResponse>> getBranchOrders(@PathVariable Long branchId,@PathVariable Long restId) {
        List<OrderResponse> orders = orderService.getAllBranchOrders(branchId,restId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/restaurant/user/{restId}/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId,@PathVariable Long restId) {
        List<OrderResponse> orders = orderService.getAllOrdersOfUser(userId,restId);
        return ResponseEntity.ok(orders);
    }
    @PatchMapping("/status/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long orderId,@RequestParam OrderStatus newStatus) {
        OrderResponse response = orderService.updateOrderStatus(orderId,newStatus);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PatchMapping("/{orderId}/ready")
    public ResponseEntity<Map<String, Object>> markOrderAsReady(@PathVariable Long orderId) {



        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order #" + orderId + " is now READY_FOR_PICKUP. Searching for nearby agents via WebSockets...");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<OrderDeliveryResponse> getOrderTrackingDetails(@PathVariable Long orderId) {
        OrderDeliveryResponse response = orderService.getOrderDeliveryDetails(orderId);
        return ResponseEntity.ok(response);
    }
}