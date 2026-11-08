package com.Hatly.Backend.product.controller;

import com.Hatly.Backend.product.dto.ProductBranchResponse;
import com.Hatly.Backend.product.service.ProductBranchService;
import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class ProductBranchController {

    private final ProductBranchService productBranchService;


    public ProductBranchController(ProductBranchService productBranchService) {
        this.productBranchService = productBranchService;
    }


    @GetMapping("/{branchId}/products")
    public ResponseEntity<List<ProductBranchResponse>> findProductsByBranchId(
            @PathVariable Long branchId) {

        List<ProductBranchResponse> responses = productBranchService.findProductsByBranchId(branchId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{branchId}/categories/{categoryName}/products")
    public ResponseEntity<List<ProductBranchResponse>> getAllProductsOfCategoryByBranch(
            @PathVariable Long branchId,
            @PathVariable CategoryOfProduct categoryName) {

        List<ProductBranchResponse> products =
                productBranchService.findProductsByBranchIdAndCategoryName(branchId, categoryName);

        return ResponseEntity.ok(products);
    }
}
