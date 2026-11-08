package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.payment.enums.PaymentProviderName;

public interface PaymentStrategy {
    String processPayment(Long orderId) throws Exception;

    PaymentProviderName getProviderName();
}
