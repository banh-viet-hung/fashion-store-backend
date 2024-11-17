package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Color {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String code;

        // Một màu có thể thuộc nhiều sản phẩm thông qua ProductVariant
        @OneToMany(mappedBy = "color")
        private List<ProductVariant> productVariants;

        // Mối quan hệ với Product (n-n)
        @ManyToMany(mappedBy = "colors")
        private List<Product> products;
}
