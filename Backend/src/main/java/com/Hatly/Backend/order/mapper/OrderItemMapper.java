package com.Hatly.Backend.order.mapper;

import com.Hatly.Backend.order.model.OrderItem;
import com.Hatly.Backend.order.dto.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "productBranchDetail.id", target = "productBranchDetailId")
    OrderItemResponse toResponse(OrderItem orderItem);


}