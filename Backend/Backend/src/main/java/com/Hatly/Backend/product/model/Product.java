package com.Hatly.Backend.product.model;

import com.Hatly.Backend.resturant.model.Restaurant;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;
    @ManyToOne
    @JoinColumn(name="category_id")
    private ProductCategory category;

}
