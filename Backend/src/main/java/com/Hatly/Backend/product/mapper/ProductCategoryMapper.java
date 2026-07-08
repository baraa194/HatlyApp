package com.Hatly.Backend.product.mapper;

import com.Hatly.Backend.product.dto.RestaurantCategoriesResponse;
import com.Hatly.Backend.product.model.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {


    @Mapping(source = "restaurant.id", target = "restaurant_id")
    @Mapping(source = "createdAt", target = "created_at")
    @Mapping(source = "updatedAt", target = "updated_at")
    RestaurantCategoriesResponse toResponse(ProductCategory category);


    List<RestaurantCategoriesResponse> toResponseList(List<ProductCategory> categories);
}
