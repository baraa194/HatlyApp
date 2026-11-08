package com.Hatly.Backend.product.repo;

import com.Hatly.Backend.product.model.ProductBranchDetail;
import com.Hatly.Backend.resturant.enums.CategoryOfProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBranchRepo extends JpaRepository<ProductBranchDetail,Long> {

    List<ProductBranchDetail> findAllByBranchId(Long branchId);
    Optional<ProductBranchDetail> findByBranchIdAndProductId(Long branchId, Long productId);
    List<ProductBranchDetail> findAllByBranchIdAndProduct_Category_Name(Long branchId, CategoryOfProduct category);

}
