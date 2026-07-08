package com.Hatly.Backend.order.mapper;

import com.Hatly.Backend.order.dto.OrderDeliveryResponse;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerAddressOrderMapper.class, OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.label", target = "branchName")
    @Mapping(source = "restaurant.name", target = "restaurantName")
    @Mapping(source = "customerAddress", target = "customerAddress")
    @Mapping(source = "orderItems", target = "items")
    @Mapping(source = "paymentProvider.providerName", target = "paymentProviderName")


    OrderResponse toResponse(Order order);
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "status", target = "orderStatus")
    @Mapping(source = "paymentProvider.providerName", target = "providerName")

    @Mapping(source = "deliveryAgent.id", target = "agentId")
    @Mapping(source = "deliveryAgent.vehicleType", target = "vehicleType")
    @Mapping(source = "deliveryAgent.vehicleNumber", target = "vehicleNumber")


    @Mapping(source = "deliveryAgent.user.name", target = "agentName")
    @Mapping(source = "deliveryAgent.user.phone", target = "agentPhone")
    @Mapping(source = "deliveryAgent.agentPresence.lastLat", target = "lat")
    @Mapping(source = "deliveryAgent.agentPresence.lastLng", target = "lng")
    OrderDeliveryResponse toDeliveryResponse(Order order);


}