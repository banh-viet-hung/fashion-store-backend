package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FavoriteProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Size sản phẩm
    private String size;

    // Màu sản phẩm
    private String color;

    // Quan hệ n-1 với User
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;

    // Quan hệ n-1 với Product
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;
}
