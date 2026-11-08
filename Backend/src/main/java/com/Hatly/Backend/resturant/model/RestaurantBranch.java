package com.Hatly.Backend.resturant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "restaurant_branches")
@Data
public class RestaurantBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addressDetailed;

    private BigDecimal lat;

    private BigDecimal lng;

    private Integer deliveryRadius;

    private String currency;

    private LocalTime openAt;

    private LocalTime closeAt;
    private Boolean acceptOrders;
    private String label;
    private Boolean IsActive;
    private BigDecimal commission;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
