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

    @Lob
    private String description;

    private int quantity;

    // Một sản phẩm có thể có nhiều size
    // Quan hệ n-n với Size
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_size", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "size_id"))
    private Set<Size> sizes;

    // Một sản phẩm có thể có nhiều màu
    // Quan hệ n-n với Color
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_color", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "color_id"))
    private Set<Color> colors;

    private String brand;

    // mô tả chi tiết
    @Lob
    private String detail;

    // ngày tạo sản phẩm
    private LocalDateTime createdAt;

    // ngày cập nhật sản phẩm
    private LocalDateTime updatedAt;

    // Một sản phẩm có thể có nhiều ảnh
    // Quan hệ 1-n với Image
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Image> images;

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

    // Một sản phẩm có thể nằm trong nhiều giỏ hàng
    // Quan hệ 1-n với CartProduct
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<CartProduct> cartProducts;

    // Các sản phẩm liên quan
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "related_product", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "related_id"))
    private Set<Product> relatedProducts;

    // Một sản phẩm có nhiều tính năng
    // Quan hệ n-n với Feature
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_feature", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private Set<Feature> features;
}
