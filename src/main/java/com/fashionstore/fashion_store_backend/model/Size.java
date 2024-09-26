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

    private String description;

    // Một size có thể thuộc nhiều sản phẩm
    // Quan hệ n-n với Product
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_size", joinColumns = @JoinColumn(name = "size_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;
}
