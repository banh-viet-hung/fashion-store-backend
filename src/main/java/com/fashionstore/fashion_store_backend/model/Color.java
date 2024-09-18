package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Color {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private String code;

        private String image;

        private String description;

        // Một màu có thể thuộc nhiều sản phẩm
        // Quan hệ n-n với Product
        @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
        @JoinTable(name = "product_color", joinColumns = @JoinColumn(name = "color_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
        private Set<Product> products;
}
