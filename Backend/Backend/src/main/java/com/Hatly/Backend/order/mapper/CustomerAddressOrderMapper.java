package com.Hatly.Backend.order.mapper;

import com.Hatly.Backend.user.model.CustomerAddress;
import com.Hatly.Backend.order.dto.CustomerAddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CustomerAddressOrderMapper {

    @Mapping(target = "addressText", source = "address", qualifiedByName = "buildAddressText")
    CustomerAddressResponse toResponse(CustomerAddress address);

    @Named("buildAddressText")
    default String buildAddressText(CustomerAddress address) {
        if (address == null) return null;
        StringBuilder sb = new StringBuilder();
        if (address.getBuilding() != null) sb.append("building ").append(address.getBuilding()).append(", ");
        if (address.getApartmentNumber() != null) sb.append("appartment ").append(address.getApartmentNumber()).append(", ");
        sb.append(address.getStreet()).append(", ").append(address.getCity());
        return sb.toString();
    }
}