package com.Hatly.Backend.product.dto;

import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestaurantCategoriesResponse {
    private Long id;
    private Long restaurant_id;
    private CategoryOfProduct name;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
