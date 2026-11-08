package com.Hatly.Backend.resturant.model;

import com.Hatly.Backend.payment.model.PaymentProvider;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="restaurent_balances")
public class RestaurentBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int balance;
    private LocalDateTime updatedat;
}
