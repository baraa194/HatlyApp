package com.Hatly.Backend.resturant.controller;

import com.Hatly.Backend.common.StorageService;
import com.Hatly.Backend.resturant.dto.CreateRestaurantDTO;
import com.Hatly.Backend.resturant.dto.RestaurantRequest;
import com.Hatly.Backend.resturant.dto.RestaurantResponse;
import com.Hatly.Backend.resturant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private StorageService  storageService;

    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createRestaurant(
            @Valid @ModelAttribute CreateRestaurantDTO dto,
            @RequestParam("image") MultipartFile image) {


        String imageUrl = storageService.uploadFile(image, "restaurants");

        dto.setLogoUrl(imageUrl);
        Map<String, Object> result = restaurantService.createRestaurant(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        RestaurantResponse response = restaurantService.getRestaurant(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequest req) {
        RestaurantResponse response = restaurantService.updateRestaurant(id, req);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Restaurant and its members deleted successfully");
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody String  status) {

        Map<String, Object> result = restaurantService.changeStatus(id,status);
        return ResponseEntity.ok(result);
    }
}