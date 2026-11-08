package com.Hatly.Backend.payment.model;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_providers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentProviderName providerName;
    @Column(name = "is_enabled")
    private boolean isEnabled;
    private int priority;
}
