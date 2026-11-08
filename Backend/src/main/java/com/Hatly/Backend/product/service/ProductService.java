package com.Hatly.Backend.product.service;

import com.Hatly.Backend.product.dto.*;
import com.Hatly.Backend.product.mapper.ProductBranchMapper;
import com.Hatly.Backend.product.mapper.ProductCategoryMapper;
import com.Hatly.Backend.product.mapper.ProductResponseMapper;
import com.Hatly.Backend.product.model.Product;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import com.Hatly.Backend.product.model.ProductCategory;
import com.Hatly.Backend.product.repo.ProductBranchRepo;
import com.Hatly.Backend.product.repo.ProductCategoryRepo;
import com.Hatly.Backend.product.repo.ProductRepo;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.resturant.repo.RestBranchRepo;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    RestaurantRepo restaurantRepo;
    @Autowired
    ProductCategoryRepo productCategoryRepo;
    @Autowired
    RestBranchRepo branchRepo;
    @Autowired
    ProductBranchRepo productBranchrepo;
    @Autowired
    ProductResponseMapper responseMapper;
    @Autowired
    ProductCategoryMapper productCategoryMapper;
    @Autowired
    ProductBranchMapper  productBranchMapper;

    @CacheEvict(value = {"restaurant_products", "branch_products"}, allEntries = true)
    public ProductResponse CreateProduct(Long restaurant_id,ProductRequest productRequest) {
        Restaurant restaurant = restaurantRepo.findById(restaurant_id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        ProductCategory category = productCategoryRepo.findByNameAndRestaurantId(productRequest.getCategoryName(), restaurant_id)
                .orElseThrow(() -> new RuntimeException("Category '" + productRequest.getCategoryName() + "' not found for restaurant id: " + restaurant_id));

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setImgUrl(productRequest.getImgUrl());
        product.setRestaurant(restaurant);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepo.save(product);

        List<RestaurantBranch> branches = branchRepo.findByRestaurantId(restaurant.getId());
        List<ProductBranchDetail> branchDetails = branches.stream().map(branch -> {
            ProductBranchDetail detail = new ProductBranchDetail();
            detail.setProduct(savedProduct);
            detail.setBranch(branch);
            detail.setPrice(BigDecimal.ZERO);
            detail.setStock(0L);
            detail.setIsAvailable(false);
            return detail;
        }).collect(Collectors.toList());


        productBranchrepo.saveAll(branchDetails);

    return responseMapper.toResponse(savedProduct);


    }
    @Cacheable(value = "restaurant_categories", key = "#restaurantId")
   public List<RestaurantCategoriesResponse> findCategoriesByRestaurant(Long restaurantId)
   {
       List<ProductCategory> categories=
               productCategoryRepo.findAllByRestaurantId(restaurantId)
                       .orElseThrow(() -> new RuntimeException("categories not found"));
       return productCategoryMapper.toResponseList(categories);

   }

   // get products by res_id
   @Cacheable(value = "restaurant_products", key = "#restaurantId")
    public List<ProductResponse> findProductsByRestaurantId(Long restaurantId)
    {
        List<Product> products=productRepo.findAllByRestaurantId(restaurantId)
                .orElseThrow(() -> new RuntimeException("products not found"));

        return responseMapper.toResponseList(products);
    }
    @Cacheable(value = "product", key = "#id")
    public ProductResponse findProductById(Long id)
    {
        Product product=productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("product not found"));
        return responseMapper.toResponse(product);
    }


    @Transactional
    @CacheEvict(value = {"product", "restaurant_products", "branch_products"}, allEntries = true)
    public UpdateProductResponse updateProduct(Long productId, Long branchId, UpdateProductRequest req) {

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("product not found"));

        if (req.getName() != null) product.setName(req.getName());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getImageUrl() != null) product.setImgUrl(req.getImageUrl());

        if (req.getCategoryName() != null) {
            ProductCategory category = productCategoryRepo.findByName(req.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("category not found"));


            if (!category.getRestaurant().getId().equals(product.getRestaurant().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this category not belong to the restaurant");
            }
            product.setCategory(category);
        }

        Product updatedProduct = productRepo.save(product);

       // check branch
        ProductBranchDetail updatedBranchDetail = null;
        if (branchId != null && (req.getPrice() != null || req.getStock() != null || req.getIsAvailable() != null)) {


            ProductBranchDetail branchDetail = productBranchrepo
                    .findByBranchIdAndProductId(branchId, productId)
                    .orElseThrow(() -> new RuntimeException("product not found in this branch"));

            if (req.getPrice() != null) branchDetail.setPrice(req.getPrice());
            if (req.getStock() != null) branchDetail.setStock(req.getStock());
            if (req.getIsAvailable() != null) branchDetail.setIsAvailable(req.getIsAvailable());

            updatedBranchDetail = productBranchrepo.save(branchDetail);
        }


        UpdateProductResponse response = new UpdateProductResponse();
        response.setMessage("Data is updated Successfully");
        response.setProduct(responseMapper.toResponse(updatedProduct));

        if (updatedBranchDetail != null) {
            response.setBranchDetails(productBranchMapper.toBranchSummary(updatedBranchDetail));
        }

        return response;
    }



}
