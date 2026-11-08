package com.Hatly.Backend.product.repo;

import com.Hatly.Backend.product.model.ProductCategory;
import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepo extends JpaRepository<ProductCategory,Long> {
    Optional<ProductCategory> findById(Long categoryId);
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.restaurant.id = :restaurantId")
    Optional<List<ProductCategory>> findAllByRestaurantId(Long restaurantId);
    Optional<ProductCategory> findByName(CategoryOfProduct name);
    Optional<ProductCategory> findByNameAndRestaurantId(CategoryOfProduct name, Long restaurantId);
}
