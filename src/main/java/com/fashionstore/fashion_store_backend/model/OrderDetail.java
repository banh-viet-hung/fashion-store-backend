package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // San phẩm trong chi tiết đơn hàng
    // Quan hệ n-1 với Product
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;

    // Số lượng sản phẩm
    private int quantity;

    // Thuộc đơn hàng nào
    // Quan hệ n-1 với Order
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Order order;

    // Giá sản phẩm
    private double price;
}
