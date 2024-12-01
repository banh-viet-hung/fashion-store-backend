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
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;

    // Số lượng sản phẩm
    private int quantity;

    // Size sản phẩm
    private String size;

    // Màu sản phẩm
    private String color;

    // Giá sản phẩm
    private double price;

    // Thuộc đơn hàng nào
    // Quan hệ n-1 với Order
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Order order;
}
