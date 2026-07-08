package com.Hatly.Backend.user.mapper;

import com.Hatly.Backend.user.dto.CustomerAddressdto;
import com.Hatly.Backend.user.model.CustomerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="Spring")
public interface CustomerAddressMapper {

    CustomerAddressdto toDto(CustomerAddress customerAddress);



    @Mapping(target = "user", ignore = true)
    CustomerAddress toEntity(CustomerAddressdto dto);

}
