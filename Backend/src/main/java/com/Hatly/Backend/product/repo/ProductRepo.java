package com.Hatly.Backend.product.repo;

import com.Hatly.Backend.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {

    Optional<List<Product>> findAllByRestaurantId(Long restaurantId);
    Optional<Product> findById(Long id);

}
