package com.Hatly.Backend.product.controller;

import com.Hatly.Backend.product.dto.ProductCategoryRequest;
import com.Hatly.Backend.product.dto.RestaurantCategoriesResponse;
import com.Hatly.Backend.product.service.ProductCategoryService;
import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<RestaurantCategoriesResponse> addCategory(@RequestBody ProductCategoryRequest request) {
        RestaurantCategoriesResponse response = categoryService.addCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public ResponseEntity<?> findallCategories() {
        List<String> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
