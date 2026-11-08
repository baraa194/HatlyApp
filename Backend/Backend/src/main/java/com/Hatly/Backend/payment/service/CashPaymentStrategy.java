package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy implements PaymentStrategy{
    private final CashPaymentService cashPaymentService;

    public CashPaymentStrategy(CashPaymentService cashPaymentService) {
        this.cashPaymentService = cashPaymentService;
    }

    @Override
    public String processPayment(Long orderId) throws Exception {
        cashPaymentService.processCashPayment(orderId);
        return "Order Success";
    }

    @Override
    public PaymentProviderName getProviderName() {
        return PaymentProviderName.CASH;
    }
}
