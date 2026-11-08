package com.Hatly.Backend.product.mapper;

import com.Hatly.Backend.product.dto.ProductResponse;
import com.Hatly.Backend.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel="spring")
public interface ProductResponseMapper {
    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toResponse(Product product);
    List<ProductResponse> toResponseList(List<Product> products);
}
