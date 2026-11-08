package com.Hatly.Backend.user.model;

import com.Hatly.Backend.user.enums.AddressType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "customer_addresses",indexes={
        @Index(name="idx_customer_addresses_user_id",columnList = "user_id")
})
@Data
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal lat;
    @Column(nullable = false)
    private BigDecimal lng;
    @Column(nullable = false)
    private String label;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String city;
    private String apartmentNumber;
    private String building;
    @Enumerated(EnumType.STRING)
    private AddressType type;
    @Column(nullable = false)
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
