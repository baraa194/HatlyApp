package com.Hatly.Backend.product.service;

import com.Hatly.Backend.product.dto.ProductCategoryRequest;
import com.Hatly.Backend.product.dto.RestaurantCategoriesResponse;
import com.Hatly.Backend.product.mapper.ProductCategoryMapper;
import com.Hatly.Backend.product.model.ProductCategory;
import com.Hatly.Backend.product.repo.ProductCategoryRepo;
import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepo categoryRepository;
    private final RestaurantRepo restaurantRepository;
    private final ProductCategoryMapper categoryMapper;

    public RestaurantCategoriesResponse addCategory(ProductCategoryRequest request) {

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + request.getRestaurantId()));


        ProductCategory category = new ProductCategory();
        category.setName(request.getName());
        category.setRestaurant(restaurant);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());


        ProductCategory savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    public List<String> getAllCategories() {
        return Stream.of(CategoryOfProduct.values())
                .map(CategoryOfProduct::name)
                .toList();
    }

}
