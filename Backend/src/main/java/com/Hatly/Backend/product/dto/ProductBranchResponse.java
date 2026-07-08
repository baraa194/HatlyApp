package com.Hatly.Backend.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductBranchResponse {
    private Long id;
    private String name;
    private String description;
    private String imgUrl;
    private Long restaurantId;
    private Long categoryId;
    private String categoryName;
    private Boolean isAvailable;
    private BigDecimal price;
    private Long stock;
}
