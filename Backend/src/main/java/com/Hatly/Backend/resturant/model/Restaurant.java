package com.Hatly.Backend.resturant.model;

import com.Hatly.Backend.resturant.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants",indexes = {
      @Index(name="idx_resturants_status",columnList = "status"),
        @Index(name="idx_resturants_created_at",columnList = "createdAt"),


})
@Data
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;
    @Column(nullable = false)
    private String logoUrl;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private LocalDateTime statusUpdatedAt;

}
