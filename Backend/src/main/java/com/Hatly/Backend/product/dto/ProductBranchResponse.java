package com.Hatly.Backend.product.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductBranchResponse implements Serializable {
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
