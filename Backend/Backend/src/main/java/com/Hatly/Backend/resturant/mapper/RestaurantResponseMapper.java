package com.Hatly.Backend.resturant.mapper;

import com.Hatly.Backend.resturant.dto.RestaurantResponse;
import com.Hatly.Backend.resturant.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface RestaurantResponseMapper {
    @Mapping(target = "id", source = "restaurant.id")
    @Mapping(target = "name", source = "restaurant.name")
    @Mapping(target = "status", source = "restaurant.status")
    @Mapping(target = "logoUrl", source = "restaurant.logoUrl")
    @Mapping(target = "createdAt", source = "restaurant.createdAt")
    @Mapping(target = "updatedAt", source = "restaurant.updatedAt")
    @Mapping(target = "ownerId", source = "ownerId")
    RestaurantResponse toResponse(Restaurant restaurant, Long ownerId);


}
