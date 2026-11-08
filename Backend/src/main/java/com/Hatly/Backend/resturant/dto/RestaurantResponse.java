package com.Hatly.Backend.resturant.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class RestaurantResponse implements Serializable {
    private Long id;
    private Long ownerId;
    private String name;
    private String logoUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
