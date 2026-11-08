package com.Hatly.Backend.resturant.mapper;

import com.Hatly.Backend.resturant.dto.BranchResponse;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface BranchResponseMapper {
    @Mapping(source = "restaurant.id", target = "restaurant_id")

    BranchResponse toResponse(RestaurantBranch branch);

}
