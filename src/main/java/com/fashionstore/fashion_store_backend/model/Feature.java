package com.fashionstore.fashion_store_backend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Feature {

    // id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // name
    private String name;

    // description
    private String description;

    // Quan hệ n-n với Product
    // Một feature có thể thuộc nhiều sản phẩm
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_feature", joinColumns = @JoinColumn(name = "feature_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products;
}
