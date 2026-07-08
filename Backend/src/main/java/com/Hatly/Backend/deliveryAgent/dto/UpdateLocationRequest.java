package com.Hatly.Backend.deliveryAgent.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class UpdateLocationRequest {
    private BigDecimal lastLat;
    private BigDecimal lastLng;
}
