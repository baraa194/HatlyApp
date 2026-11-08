package com.Hatly.Backend.resturant.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class UpdateBranchStatusRequest {
    private Boolean isActive;
    private BigDecimal commission;
}
