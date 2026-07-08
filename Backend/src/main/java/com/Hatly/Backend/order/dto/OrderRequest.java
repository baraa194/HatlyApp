package com.Hatly.Backend.order.dto;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private Long restaurant_id;
    private Long restaurnat_branch_id;
    private Long customerAddress_id;
    private BigDecimal deliveryLat;
    private BigDecimal deliveryLng;
    private PaymentProviderName paymentProviderName;
    private List<OrderItemRequest> items;


}
