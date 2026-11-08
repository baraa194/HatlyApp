package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentStrategy implements PaymentStrategy{
    private final StripePaymentService stripePaymentService;

    public StripePaymentStrategy(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @Override
    public String processPayment(Long orderId) throws Exception {

        return stripePaymentService.createStripeSession(orderId);
    }

    @Override
    public PaymentProviderName getProviderName() {
        return PaymentProviderName.STRIPE;
    }
}
