package com.Hatly.Backend.payment.controller;

import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.payment.dto.PaymentProviderResponse;
import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.repo.PaymentProviderRepo;
import com.Hatly.Backend.payment.repo.PaymentRepo;
import com.Hatly.Backend.payment.service.StripePaymentService;
import com.Hatly.Backend.payment.service.PaymentStrategy;
import com.Hatly.Backend.payment.service.PaymentStrategyFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final StripePaymentService stripePaymentService;
    private final PaymentStrategyFactory strategyFactory;
    private final PaymentProviderRepo paymentProviderRepo;
    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;


    public PaymentController(StripePaymentService stripePaymentService,
                             PaymentStrategyFactory strategyFactory,
                             PaymentProviderRepo paymentProviderRepo, OrderRepo orderRepo,
                             PaymentRepo paymentRepo) {
        this.stripePaymentService = stripePaymentService;
        this.strategyFactory = strategyFactory;
        this.paymentProviderRepo = paymentProviderRepo;
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
    }

    @GetMapping("/providers")
    public ResponseEntity<List<PaymentProviderResponse>> getAvailableProviders() {
        return ResponseEntity.ok(stripePaymentService.getAvailableProviders());
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        PaymentProviderName providerName = PaymentProviderName.valueOf(request.get("providerName").toString().toUpperCase());

        try {

            PaymentStrategy strategy = strategyFactory.getStrategy(providerName);
            String resultUrl = strategy.processPayment(orderId);

            return ResponseEntity.ok(Map.of("resultUrl", resultUrl));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            stripePaymentService.processWebhook(payload, sigHeader);
            return ResponseEntity.ok("Webhook verified and processed successfully");
        } catch (Exception e) {

            return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> handleSuccessPayment(@RequestParam("id") Long orderId) {
        stripePaymentService.markOrderAsPaid(orderId);
        return ResponseEntity.ok("Order marked as PAID successfully!");
    }
}