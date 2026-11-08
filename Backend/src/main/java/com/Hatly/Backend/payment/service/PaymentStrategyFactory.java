package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyFactory {
    private final Map<PaymentProviderName, PaymentStrategy> strategies;


    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getProviderName, strategy -> strategy));
    }

    public PaymentStrategy getStrategy(PaymentProviderName providerName) {
        PaymentStrategy strategy = strategies.get(providerName);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment provider: " + providerName);
        }
        return strategy;
    }
}
