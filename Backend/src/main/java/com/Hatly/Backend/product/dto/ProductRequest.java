package com.Hatly.Backend.product.dto;

import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private String imgUrl;
    @JsonProperty("category_name")
    private CategoryOfProduct CategoryName;
}
