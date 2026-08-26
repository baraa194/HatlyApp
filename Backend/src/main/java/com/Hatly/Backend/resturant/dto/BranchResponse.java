package com.Hatly.Backend.resturant.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class BranchResponse implements Serializable {
    private Long id;
    private String addressDetailed;

    private BigDecimal lat;

    private BigDecimal lng;

    private Integer deliveryRadius;

    private String currency;

    private LocalTime openAt;

    private LocalTime closeAt;
    private boolean acceptOrders;
    private String label;
    private Integer commission;
    private Boolean IsActive;
    private Long restaurant_id;

}
