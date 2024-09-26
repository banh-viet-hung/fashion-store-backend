package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sản phẩm trong giỏ hàng
    // Quan hệ n-1 với Product
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;

    // Số lượng sản phẩm
    private int quantity;

    // Size sản phẩm
    private String size;

    // Màu sản phẩm
    private String color;

    // Thuộc user nào
    // Quan hệ n-1 với User
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;

    // Giá sản phẩm
    private double totalPrice;
}
