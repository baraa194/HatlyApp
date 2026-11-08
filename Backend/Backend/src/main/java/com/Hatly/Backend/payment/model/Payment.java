package com.Hatly.Backend.payment.model;

import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.payment.enums.Currency;
import com.Hatly.Backend.payment.enums.PaymentMethod;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerReferenceId;

    private BigDecimal amount;

    private String idempotencyKey;

    private boolean refunded;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name="payment_provider_id")
    private PaymentProvider provider;
}
