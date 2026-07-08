package com.Hatly.Backend.user.dto;

import com.Hatly.Backend.resturant.dto.RestaurantRequest;
import com.Hatly.Backend.user.enums.systemRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    @Enumerated(EnumType.STRING)
    private systemRole role;
    @Valid
    private RestaurantRequest restaurant;

}
