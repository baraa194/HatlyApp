package com.Hatly.Backend.order.dto;

import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderResponse {
    private Long id;
    private Integer amount;
    private Integer serviceFee;
    private Integer deliveryFee;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;

    private Long customerId;
    private String customerName;
    private CustomerAddressResponse customerAddress;

    private Long branchId;
    private String branchName;
    private String restaurantName;
    private String providerName;
    private PaymentProviderName paymentProviderName;
    private PaymentStatus paymentStatus;

    private List<OrderItemResponse> items;

}
