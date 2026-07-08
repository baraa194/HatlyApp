package com.Hatly.Backend.product.model;

import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import com.Hatly.Backend.resturant.model.Restaurant;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_categories")
@Data
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private CategoryOfProduct name;
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




}
