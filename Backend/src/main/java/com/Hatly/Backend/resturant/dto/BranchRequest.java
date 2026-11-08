package com.Hatly.Backend.resturant.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class BranchRequest {

    private String addressDetailed;

    private BigDecimal lat;

    private BigDecimal lng;

    private Integer deliveryRadius;

    private String currency;

    private LocalTime openAt;

    private LocalTime closeAt;

    private Boolean acceptOrders;
    private String label;
    private Boolean IsActive;
    private BigDecimal commission;


}
