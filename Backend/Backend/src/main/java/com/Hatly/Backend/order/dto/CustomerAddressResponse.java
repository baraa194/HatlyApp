package com.Hatly.Backend.order.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CustomerAddressResponse {
    private Long id;
    private String addressText;
    private BigDecimal lat;
    private BigDecimal lng;
}
