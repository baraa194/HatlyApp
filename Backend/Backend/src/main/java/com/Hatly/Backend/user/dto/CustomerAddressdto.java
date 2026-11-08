package com.Hatly.Backend.user.dto;

import com.Hatly.Backend.user.enums.AddressType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerAddressdto {

    private Long id;
    private String label;
    private String street;
    private String city;
    private String apartmentNumber;
    private String building;
    @Enumerated(EnumType.STRING)
    private AddressType type;
    private BigDecimal lat;
    private BigDecimal lng;
    private boolean isDefault;
}
