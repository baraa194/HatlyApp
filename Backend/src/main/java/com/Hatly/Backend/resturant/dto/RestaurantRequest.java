package com.Hatly.Backend.resturant.dto;

import com.Hatly.Backend.resturant.enums.RestaurantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestaurantRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String logoUrl;
}
