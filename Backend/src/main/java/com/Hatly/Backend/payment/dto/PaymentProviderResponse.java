package com.Hatly.Backend.payment.dto;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProviderResponse {
    private Long id;
    private PaymentProviderName providerName;
    private int priority;
}
