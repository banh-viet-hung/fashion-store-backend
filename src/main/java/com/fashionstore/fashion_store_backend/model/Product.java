package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // giá niêm yết
    private double price;

    // giá thực tế
    private double salePrice;

    // Đánh giá trung bình
    private double averageRating;

    private String description;

    // Một sản phẩm có thể có nhiều ảnh
    // Quan hệ 1-n với Image
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Image> images;

    private int quantity;

    private String size;

    private String color;

    private String brand;

    // ngày tạo sản phẩm
    private LocalDateTime createdAt;

    // ngày cập nhật sản phẩm
    private LocalDateTime updatedAt;

    // Quan hệ n-n với Category
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    // Một sản phẩm có nhiều feedback
    // Quan hệ 1-n với Feedback
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Feedback> feedbacks;

    // Danh sách chi tiết đơn hàng
    // Quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Set<OrderDetail> orderDetails;

    // Quan hệ 1-n với FavoriteProduct
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FavoriteProduct> favoriteProducts;
}
