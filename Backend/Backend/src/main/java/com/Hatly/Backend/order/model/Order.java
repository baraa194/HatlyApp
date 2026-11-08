package com.Hatly.Backend.order.model;

import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.payment.model.PaymentProvider;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.user.model.CustomerAddress;
import com.Hatly.Backend.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;


    private BigDecimal total;

    private BigDecimal deliveryLat;

    private BigDecimal deliveryLng;

    private Integer serviceFee;

    private Integer deliveryFee;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name="resturant_id")
    private Restaurant restaurant;
    @ManyToOne
    @JoinColumn(name="restaurantbranch_id")
    private RestaurantBranch branch;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name="customerAddress_id")
    private CustomerAddress customerAddress;
    @ManyToOne
    @JoinColumn(name = "payment_provider_id")
    private PaymentProvider paymentProvider;

    @ManyToOne
    @JoinColumn(name = "delivery_agent__id")
    private DeliveryAgent deliveryAgent;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}
