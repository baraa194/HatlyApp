package com.Hatly.Backend.product.mapper;

import com.Hatly.Backend.product.dto.BranchDetailsSummary;
import com.Hatly.Backend.product.dto.ProductBranchResponse;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel="spring")
public interface ProductBranchMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.description", target = "description")
    @Mapping(source = "product.imgUrl", target = "imgUrl")
    @Mapping(source = "product.restaurant.id", target = "restaurantId")
    @Mapping(source = "product.category.id", target = "categoryId")
    @Mapping(source = "product.category.name", target = "categoryName")
    @Mapping(source = "isAvailable", target = "isAvailable")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "stock", target = "stock")
    ProductBranchResponse toBranchResponse(ProductBranchDetail detail);


    List<ProductBranchResponse> toBranchResponseList(List<ProductBranchDetail> details);

    @Mapping(source = "isAvailable", target = "isAvailable")
    BranchDetailsSummary toBranchSummary(ProductBranchDetail detail);
}
