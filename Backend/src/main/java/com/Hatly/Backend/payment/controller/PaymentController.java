package com.Hatly.Backend.payment.controller;

import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.payment.dto.PaymentProviderResponse;
import com.Hatly.Backend.payment.enums.Currency;
import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.payment.model.Payment;
import com.Hatly.Backend.payment.model.PaymentProvider;
import com.Hatly.Backend.payment.repo.PaymentProviderRepo;
import com.Hatly.Backend.payment.repo.PaymentRepo;
import com.Hatly.Backend.payment.service.PaymentService;
import com.Hatly.Backend.payment.service.PaymentStrategy;
import com.Hatly.Backend.payment.service.PaymentStrategyFactory;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentStrategyFactory strategyFactory;
    private final PaymentProviderRepo paymentProviderRepo;
    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;


    public PaymentController(PaymentService paymentService,
                             PaymentStrategyFactory strategyFactory,
                             PaymentProviderRepo paymentProviderRepo,OrderRepo orderRepo,
                             PaymentRepo paymentRepo){
        this.paymentService = paymentService;
        this.strategyFactory= strategyFactory;
        this.paymentProviderRepo = paymentProviderRepo;
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
    }

    @GetMapping("/providers")
    public ResponseEntity<List<PaymentProviderResponse>> getAvailableProviders() {
        return ResponseEntity.ok(paymentService.getAvailableProviders());
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
            paymentService.processWebhook(payload, sigHeader);
            return ResponseEntity.ok("Webhook verified and processed successfully");
        } catch (Exception e) {

            return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
        }
    }
    @GetMapping("/success")
    public ResponseEntity<String> handleSuccessPayment(@RequestParam("id") Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order id not found"));


        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.PREPARING);
        orderRepo.save(order); // حفظ تحديث الطلب


        PaymentProvider stripeProvider = paymentProviderRepo.findByProviderName(PaymentProviderName.STRIPE)
                .orElseThrow(() -> new RuntimeException("Payment Provider STRIPE not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotal());
        payment.setStatus(PaymentStatus.PAID);
        payment.setCurrency(Currency.EGP);
        payment.setRefunded(false);
        payment.setProvider(stripeProvider);

        paymentRepo.save(payment);

        return ResponseEntity.ok("Payment saved successfully in Database!");
    }
}