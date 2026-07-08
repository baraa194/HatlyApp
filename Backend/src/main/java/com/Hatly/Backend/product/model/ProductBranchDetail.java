package com.Hatly.Backend.product.model;

import com.Hatly.Backend.resturant.model.RestaurantBranch;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "product_branch_details")
@Data
public class ProductBranchDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isAvailable;

    private BigDecimal price;

    private Long stock;

    @ManyToOne
    private Product product;

    @ManyToOne
    private RestaurantBranch branch;
}
