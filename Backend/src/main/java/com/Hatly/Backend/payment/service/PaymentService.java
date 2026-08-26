package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.payment.dto.PaymentProviderResponse;
import com.Hatly.Backend.payment.enums.Currency;
import com.Hatly.Backend.payment.enums.PaymentMethod;
import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.payment.model.Payment;
import com.Hatly.Backend.payment.model.PaymentProvider;
import com.Hatly.Backend.payment.repo.PaymentProviderRepo;
import com.Hatly.Backend.payment.repo.PaymentRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;



@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentProviderRepo paymentProviderRepo;
    @Autowired
    private OrderRepo orderrepo;
    @Autowired
    PaymentRepo paymentrepo;
    @Autowired
    PaymentProviderRepo paymentProviderrepo;
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public List<PaymentProviderResponse> getAvailableProviders() {
        List<PaymentProvider> providers = paymentProviderRepo.findByIsEnabledTrueOrderByPriorityAsc()
                .orElseThrow(() -> new RuntimeException("Provider not found"));


        return providers.stream()
                .map(provider -> new PaymentProviderResponse(
                        provider.getId(),
                        provider.getProviderName(),
                        provider.getPriority()
                ))
                .collect(Collectors.toList());
    }

    public String createStripeSession(Long orderId) throws StripeException {

        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order id not found"));
        Optional<Payment> existingPayment = paymentrepo
                .findByOrderIdAndStatus(orderId, PaymentStatus.PENDING);

        String idempotencyKey;
        Payment payment;

        if (existingPayment.isPresent()) {
            payment = existingPayment.get();
            idempotencyKey = payment.getIdempotencyKey();
        } else {

            idempotencyKey = "hatly_order_" + orderId ;

            PaymentProvider stripeProvider = paymentProviderrepo
                    .findByProviderName(PaymentProviderName.STRIPE)
                    .orElseThrow(() -> new RuntimeException("Payment Provider STRIPE not found"));

            payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotal());
            payment.setIdempotencyKey(idempotencyKey);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCurrency(Currency.EGP);
            payment.setRefunded(false);
            payment.setProvider(stripeProvider);


            paymentrepo.save(payment);


        }
        long orderAmountInCents = order.getTotal()
                .multiply(new BigDecimal(100)).longValue();


        SessionCreateParams params = SessionCreateParams.builder()
                .setClientReferenceId(orderId.toString())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)

                .setSuccessUrl("http://localhost:4200/order-success?id=" + orderId)
                .setCancelUrl("https://hatlyapp.com/order/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("egp")
                                                .setUnitAmount(orderAmountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Hatly Order #" + orderId)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();

        Session session = Session.create(params, requestOptions);


        payment.setProviderReferenceId(session.getId());
        paymentrepo.save(payment);





        return session.getUrl();
    }

    @Transactional
    public void processWebhook(String payload, String sigHeader) throws Exception {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        if (!"checkout.session.completed".equals(event.getType())) {
            return;
        }

        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (session == null) {
            log.warn("checkout.session.completed with null session object");
            return;
        }

        String orderIdStr = session.getClientReferenceId();
        if (orderIdStr == null) {
            log.error("Session {} has no client_reference_id", session.getId());
            return;
        }

        Long orderId = Long.parseLong(orderIdStr);
        log.info("Processing checkout.session.completed for orderId={}, sessionId={}", orderId, session.getId());

        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));


        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            log.info("Order {} already PAID, skipping", orderId);
            return;
        }

        Payment payment = paymentrepo.findByProviderReferenceId(session.getId())
                .orElseGet(() -> paymentrepo.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING)
                        .orElseThrow(() -> new RuntimeException(
                                "No pending payment found for order " + orderId + " / session " + session.getId())));

        PaymentProvider stripeProvider = paymentProviderrepo
                .findByProviderName(PaymentProviderName.STRIPE)
                .orElseThrow(() -> new RuntimeException("STRIPE provider not found"));


        payment.setStatus(PaymentStatus.PAID);
        payment.setAmount(BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100)));
        payment.setCurrency(Currency.EGP);
        payment.setRefunded(false);
        payment.setProvider(stripeProvider);
        payment.setMethod(PaymentMethod.CARD);

        paymentrepo.save(payment);


        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.PREPARING);
        orderrepo.save(order);

        log.info("Payment Successful for orderId={}, paymentId={}, sessionId={}",
                orderId, payment.getId(), session.getId());


    }

    @Transactional
    public void markOrderAsPaid(Long orderId) {
        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));


        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            log.info("Order {} is already PAID, skipping", orderId);
            return;
        }


        Payment payment = paymentrepo.findByOrderIdAndStatus(orderId, PaymentStatus.PENDING)
                .orElseGet(() -> paymentrepo.findByOrderIdAndStatus(orderId, PaymentStatus.PAID)
                        .orElseThrow(() -> new RuntimeException("No payment record found for order: " + orderId)));


        payment.setStatus(PaymentStatus.PAID);
        payment.setMethod(PaymentMethod.CARD);
        paymentrepo.save(payment);


        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.PREPARING);
        orderrepo.save(order);

        log.info("Payment successfully marked as PAID for orderId={}, paymentId={}", orderId, payment.getId());


        publishPaymentCompletedEvent(orderId, payment.getId());
    }
    private void publishPaymentCompletedEvent(Long orderId, Long paymentId) {
        try {
            Map<String, String> eventData = new HashMap<>();
            eventData.put("orderId", orderId.toString());
            eventData.put("paymentId", paymentId.toString());
            eventData.put("eventType", "PAYMENT_COMPLETED");
            eventData.put("timestamp", Instant.now().toString());



             stringRedisTemplate.opsForStream().add(
            org.springframework.data.redis.connection.stream.StreamRecords
                    .mapBacked(eventData)
                    .withStreamKey("payment-events")
             );
        } catch (Exception e) {
            log.error("Failed to publish PaymentCompletedEvent for order {}", orderId, e);

        }
    }
}
