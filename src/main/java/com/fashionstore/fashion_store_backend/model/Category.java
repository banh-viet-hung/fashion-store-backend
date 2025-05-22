package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // slug của danh mục
    @Column(unique = true)
    private String slug;

    // Trạng thái xóa mềm
    private boolean deleted = false;

    // Quan hệ n-n với Product
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    // Quan hệ nhiều-nhiều với các danh mục cha (parent categories)
    @ManyToMany
    @JoinTable(name = "category_parent_category", joinColumns = @JoinColumn(name = "child_category_id"), inverseJoinColumns = @JoinColumn(name = "parent_category_id"))
    private List<Category> parentCategories;

    // Quan hệ nhiều-nhiều với các danh mục con (child categories)
    @ManyToMany(mappedBy = "parentCategories")
    private List<Category> childCategories;
}
