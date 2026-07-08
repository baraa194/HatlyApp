package com.Hatly.Backend.payment.service;

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
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;



@Service
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

        Session session = Session.create(params);


        return session.getUrl();
    }


    public void processWebhook(String payload, String sigHeader) throws Exception {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                String orderIdStr = session.getClientReferenceId();
                Long orderId = Long.parseLong(orderIdStr);
                Order order = orderrepo.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Order id not found"));
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setStatus(OrderStatus.PREPARING);

                PaymentProvider stripeProvider = paymentProviderrepo.findByProviderName(PaymentProviderName.STRIPE)
                        .orElseThrow(() -> new RuntimeException("Payment Provider STRIPE not found in DB"));
                Payment payment = new Payment();
                payment.setOrder(order);
                payment.setAmount(BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100)));

               // reference id if we want to refund
                payment.setProviderReferenceId(session.getPaymentIntent());

                payment.setStatus(PaymentStatus.PAID);
                payment.setCurrency(Currency.EGP);
                payment.setRefunded(false);
                payment.setProvider(stripeProvider);

                paymentrepo.save(payment);
                System.out.println("Payment Successful for session: " + session.getId());
            }
        }

    }
}
