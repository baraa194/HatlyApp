package com.Hatly.Backend.product.dto;

import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long id;
    private String name;

    private String description;

    private String imgUrl;
    private Long restaurantId;
    private Long CategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
