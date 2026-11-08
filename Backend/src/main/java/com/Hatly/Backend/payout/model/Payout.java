package com.Hatly.Backend.payout.model;

import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    private LocalDateTime createdat;
    private String notes;
    @OneToOne
    @JoinColumn(name="resturant_id")
    private Restaurant  restaurant;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;



}
