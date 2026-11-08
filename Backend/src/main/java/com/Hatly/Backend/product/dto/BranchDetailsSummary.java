package com.Hatly.Backend.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BranchDetailsSummary {
    private Long id;
    private BigDecimal price;
    private Long stock;
    private Boolean isAvailable;
}
