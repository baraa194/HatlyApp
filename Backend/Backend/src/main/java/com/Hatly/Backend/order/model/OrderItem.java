package com.Hatly.Backend.order.model;

import com.Hatly.Backend.product.model.Product;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameSnapshot;

    private Integer priceSnapshot;

    private String imgUrlSnapshot;

    private Integer quantity;

    private BigDecimal itemTotal;
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="product_branch_detail_id")
    private ProductBranchDetail productBranchDetail;

}