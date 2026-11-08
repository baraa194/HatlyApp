package com.Hatly.Backend.resturant.repo;

import com.Hatly.Backend.resturant.enums.RestaurantRole;
import com.Hatly.Backend.resturant.model.RestaurantMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestMemberRepo extends JpaRepository<RestaurantMember,Long> {
    Optional<RestaurantMember> findByRestaurantIdAndRole(Long restaurantId, RestaurantRole role);

    public void deleteByRestaurantId(Long id) ;
}
