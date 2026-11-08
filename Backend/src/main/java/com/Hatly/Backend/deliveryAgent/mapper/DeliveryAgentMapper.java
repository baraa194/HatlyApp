package com.Hatly.Backend.deliveryAgent.mapper;

import com.Hatly.Backend.deliveryAgent.dto.AgentInfoResponse;
import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryAgentMapper {

    @Mapping(source = "user.id", target = "user_id")
    AgentInfoResponse toResponse(DeliveryAgent agent);
}
