package com.Hatly.Backend.product.controller;

import com.Hatly.Backend.common.StorageService;
import com.Hatly.Backend.product.dto.*;
import com.Hatly.Backend.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    @Autowired
    private  StorageService storageService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping(value = "/restaurant/{restaurantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable Long restaurantId,
            @Valid @ModelAttribute ProductRequest productRequest,
            @RequestParam("image") MultipartFile image) {
        String imageUrl = storageService.uploadFile(image, "products");
        productRequest.setImgUrl(imageUrl);
        ProductResponse response = productService.CreateProduct(restaurantId, productRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/restaurant/{restaurantId}/categories")
    public ResponseEntity<List<RestaurantCategoriesResponse>> findCategoriesByRestaurant(
            @PathVariable Long restaurantId) {

        List<RestaurantCategoriesResponse> responses = productService.findCategoriesByRestaurant(restaurantId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ProductResponse>> findProductsByRestaurantId(
            @PathVariable Long restaurantId) {

        List<ProductResponse> responses = productService.findProductsByRestaurantId(restaurantId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        ProductResponse response = productService.findProductById(id);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UpdateProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long branchId,
            @RequestBody UpdateProductRequest req) {

        UpdateProductResponse response = productService.updateProduct(id, branchId, req);
        return ResponseEntity.ok(response);
    }
}