package com.Hatly.Backend.product.dto;

import lombok.Data;

@Data
public class UpdateProductResponse
{
    private String message;
    private ProductResponse product;
    private BranchDetailsSummary branchDetails;
}
