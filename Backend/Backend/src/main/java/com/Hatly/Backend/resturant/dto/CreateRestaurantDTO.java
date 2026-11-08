package com.Hatly.Backend.resturant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRestaurantDTO {
    @Valid
    @NotNull(message = "Owner information is required")
    private CreateRestaurantOwnerDTO owner;

    @NotBlank(message = "Restaurant name is required")
    private String name;

    private String logoUrl;
}
