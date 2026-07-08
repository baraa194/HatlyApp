package com.Hatly.Backend.resturant.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class RestaurantResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String logoUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
