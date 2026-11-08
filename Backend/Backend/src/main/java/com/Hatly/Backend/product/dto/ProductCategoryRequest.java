package com.Hatly.Backend.product.dto;

import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import lombok.Data;

@Data
public class ProductCategoryRequest {
    private CategoryOfProduct name;
    private Long restaurantId;
}
