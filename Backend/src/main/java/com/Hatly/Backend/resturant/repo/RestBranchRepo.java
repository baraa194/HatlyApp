package com.Hatly.Backend.resturant.repo;

import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestBranchRepo extends JpaRepository<RestaurantBranch, Long> {

    @Query("SELECT b FROM RestaurantBranch b WHERE b.restaurant.id = :restaurantId")
    List<RestaurantBranch> findByRestaurantId(@Param("restaurantId")Long Id);

    @Query(value = "SELECT b.*, " +
            "(6371 * acos(cos(radians(CAST(:userLat AS double precision))) * cos(radians(CAST(b.lat AS double precision))) * " +
            "cos(radians(CAST(b.lng AS double precision)) - radians(CAST(:userLng AS double precision))) + " +
            "sin(radians(CAST(:userLat AS double precision))) * sin(radians(CAST(b.lat AS double precision))))) AS distance " +
            "FROM restaurant_branches b " +
            "WHERE b.restaurant_id = :restaurantId AND b.is_active = true " +
            "ORDER BY distance ASC " +
            "LIMIT 1", nativeQuery = true)
    Optional<RestaurantBranch> findNearestBranchForRestaurant(
            @Param("restaurantId") Long restaurantId,
            @Param("userLat") BigDecimal userLat,
            @Param("userLng") BigDecimal userLng
    );


}
