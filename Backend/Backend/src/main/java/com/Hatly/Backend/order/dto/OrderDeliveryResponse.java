package com.Hatly.Backend.order.dto;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.user.model.User;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDeliveryResponse {
    private Long orderId;
    private String orderStatus;
    private PaymentStatus paymentStatus;
    private String providerName;


    private Long agentId;
    private String agentName;
    private String agentPhone;
    private String vehicleType;
    private String vehicleNumber;


    private BigDecimal lat;
    private BigDecimal lng;

}
