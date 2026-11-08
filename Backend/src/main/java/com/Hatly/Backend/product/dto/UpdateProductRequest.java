package com.Hatly.Backend.product.dto;

import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private String imageUrl;
    private CategoryOfProduct categoryName;

    // optional
    private BigDecimal price;
    private Long stock;
    private Boolean isAvailable;
}
