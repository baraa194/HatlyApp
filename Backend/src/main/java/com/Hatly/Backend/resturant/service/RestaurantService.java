package com.Hatly.Backend.resturant.service;

import com.Hatly.Backend.resturant.dto.CreateRestaurantDTO;
import com.Hatly.Backend.resturant.dto.RestaurantRequest;
import com.Hatly.Backend.resturant.dto.RestaurantResponse;
import com.Hatly.Backend.resturant.enums.MemberStatus;
import com.Hatly.Backend.resturant.enums.RestaurantRole;
import com.Hatly.Backend.resturant.enums.RestaurantStatus;
import com.Hatly.Backend.resturant.mapper.RestaurantResponseMapper;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantMember;
import com.Hatly.Backend.resturant.repo.RestMemberRepo;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import com.Hatly.Backend.user.enums.systemRole;
import com.Hatly.Backend.user.model.User;
import com.Hatly.Backend.user.repo.UserRepo;
import org.springframework.cache.annotation.Cacheable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantRepo restaurantrepo;
    @Autowired
    private RestMemberRepo memberrepo;
    @Autowired
    private RestaurantResponseMapper responseMapper;
    @Autowired
    private UserRepo userrepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Cacheable(value = "restaurant", key = "#id")
    public RestaurantResponse getRestaurant(Long id) {
     Restaurant restaurant = restaurantrepo.findById(id).
             orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        RestaurantMember owner=memberrepo.findByRestaurantIdAndRole(id, RestaurantRole.OWNER)
                .orElseThrow(() -> new RuntimeException("Owner not found" ));

        return responseMapper.toResponse(restaurant,owner.getUser().getId());

    }
    @Transactional
    @CacheEvict(value = "all_restaurants", allEntries = true)
    public Map<String,Object> createRestaurant(CreateRestaurantDTO restaurantRequest) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantRequest.getName());
        restaurant.setLogoUrl(restaurantRequest.getLogoUrl());
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurant.setStatusUpdatedAt(LocalDateTime.now());
        Restaurant savedRest=restaurantrepo.save(restaurant);

        User ownerUser = new User();
        ownerUser.setName(restaurantRequest.getOwner().getName());
        ownerUser.setEmail(restaurantRequest.getOwner().getEmail());
        ownerUser.setPhone(restaurantRequest.getOwner().getPhone());
        ownerUser.setPassword(encoder.encode(restaurantRequest.getOwner().getPassword()));
        ownerUser.setRole(systemRole.RESTAURANT_USER);
        ownerUser.setCreatedAt(LocalDateTime.now());
        ownerUser.setUpdatedAt(LocalDateTime.now());
        User savedUser = userrepo.save(ownerUser);

        RestaurantMember restaurantMember = new RestaurantMember();
        restaurantMember.setRestaurant(restaurant);
        restaurantMember.setUser(savedUser);
        restaurantMember.setRole(RestaurantRole.OWNER);
        restaurantMember.setStatus(MemberStatus.ACTIVE);
       memberrepo.save(restaurantMember);

        Map<String,Object> map = new HashMap<>();
        map.put("restaurant",savedRest);
        map.put("Owner",savedUser);
        return map;

    }

    @Transactional
    @CacheEvict(value = {"restaurant", "all_restaurants"}, key = "#id", allEntries = false, beforeInvocation = false)
    public RestaurantResponse updateRestaurant(Long id, RestaurantRequest req) {

        Restaurant existingRestaurant = restaurantrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        existingRestaurant.setName(req.getName());
        existingRestaurant.setLogoUrl(req.getLogoUrl() != null ? req.getLogoUrl() : existingRestaurant.getLogoUrl());

        existingRestaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant updatedRestaurant = restaurantrepo.save(existingRestaurant);


        RestaurantMember ownerMember = memberrepo.findByRestaurantIdAndRole(id, RestaurantRole.OWNER)
                .orElseThrow(() -> new RuntimeException("Owner not found for restaurant id: " + id));


        return responseMapper.toResponse(updatedRestaurant, ownerMember.getUser().getId());
    }
    @Cacheable(value = "all_restaurants")
   public List<RestaurantResponse> getAllRestaurants() {
       List<Restaurant> restaurants = restaurantrepo.findAll();

       return restaurants.stream().map(restaurant -> {
           RestaurantMember ownerMember = memberrepo.findByRestaurantIdAndRole(restaurant.getId(), RestaurantRole.OWNER)
                   .orElse(null);

           Long ownerId = (ownerMember != null) ? ownerMember.getUser().getId() : null;

           return responseMapper.toResponse(restaurant, ownerId);
       }).toList();

   }

    @Transactional
    @CacheEvict(value = {"restaurant", "all_restaurants"}, key = "#id")
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = restaurantrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        memberrepo.deleteByRestaurantId(id);
        restaurantrepo.delete(restaurant);
    }
    @CacheEvict(value = {"restaurant", "all_restaurants"}, key = "#id")
    public Map<String,Object> changeStatus(Long id, String status) {
        Restaurant restaurant = restaurantrepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        RestaurantStatus newStatus = RestaurantStatus.valueOf(status.toUpperCase());
        restaurant.setStatus(newStatus);
        restaurant.setStatusUpdatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant updatedRest = restaurantrepo.save(restaurant);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Restaurant status updated successfully");

        Map<String, Object> restaurantInfo = new HashMap<>();
        restaurantInfo.put("id", updatedRest.getId());
        restaurantInfo.put("status", updatedRest.getStatus().name().toLowerCase());

        response.put("restaurantInfo", restaurantInfo);
        return response;

    }



}
