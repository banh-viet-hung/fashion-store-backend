package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // Một size có thể thuộc nhiều sản phẩm thông qua ProductVariant
    @OneToMany(mappedBy = "size")
    private List<ProductVariant> productVariants;

    // Mối quan hệ với Product (n-n)
    @ManyToMany(mappedBy = "sizes")
    private List<Product> products;
}
