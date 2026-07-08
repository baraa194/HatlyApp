package com.Hatly.Backend.product.service;

import com.Hatly.Backend.product.dto.ProductBranchResponse;
import com.Hatly.Backend.product.dto.ProductResponse;
import com.Hatly.Backend.product.mapper.ProductBranchMapper;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import com.Hatly.Backend.product.repo.ProductBranchRepo;
import com.Hatly.Backend.product.repo.ProductCategoryRepo;
import com.Hatly.Backend.product.repo.ProductRepo;
import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.resturant.repo.RestBranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductBranchService {

    @Autowired
    ProductBranchRepo productBranchRepo;
    @Autowired
    ProductBranchMapper  productBranchMapper;


    // get products by branch id
    @Cacheable(value = "branch_products", key = "#branchId")
    public List<ProductBranchResponse> findProductsByBranchId(Long branchId)
    {
        List<ProductBranchDetail> branchDetails
                = productBranchRepo.findAllByBranchId(branchId);
        if (branchDetails.isEmpty()) {
            throw new RuntimeException("this restaurant does not have any branch");
        }
        return productBranchMapper.toBranchResponseList(branchDetails);

    }
    @Cacheable(value = "branch_products_by_category", key = "#branchId + '_' + #categoryName.name()")
    public List<ProductBranchResponse> findProductsByBranchIdAndCategoryName(Long branchId, CategoryOfProduct categoryName) {

        List<ProductBranchDetail> branchDetails =
                productBranchRepo.findAllByBranchIdAndProduct_Category_Name(branchId, categoryName);

        if (branchDetails.isEmpty()) {
            throw new RuntimeException("No products found for this category name in this branch");
        }

        return productBranchMapper.toBranchResponseList(branchDetails);
    }


}
