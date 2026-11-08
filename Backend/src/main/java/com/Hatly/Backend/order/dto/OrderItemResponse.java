package com.Hatly.Backend.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;

    private String nameSnapshot;

    private BigDecimal priceSnapshot;

    private String imgUrlSnapshot;

    private Integer quantity;

    private BigDecimal itemTotal;

    private Long productBranchDetailId;
}
