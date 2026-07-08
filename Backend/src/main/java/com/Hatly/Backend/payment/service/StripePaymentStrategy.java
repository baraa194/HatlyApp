package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentStrategy implements PaymentStrategy{
    private final PaymentService paymentService;

    public StripePaymentStrategy(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String processPayment(Long orderId) throws Exception {

        return paymentService.createStripeSession(orderId);
    }

    @Override
    public PaymentProviderName getProviderName() {
        return PaymentProviderName.STRIPE;
    }
}
