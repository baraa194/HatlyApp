package com.Hatly.Backend.resturant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRestaurantOwnerDTO {
    @NotBlank(message = "Owner email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Owner phone is required")
    private String phone;

    @NotBlank(message = "Owner name is required")
    private String name;

    @NotBlank(message = "Owner password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
