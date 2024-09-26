package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
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
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_size", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "size_id"))
    private List<Size> sizes;

    // Một sản phẩm có thể có nhiều màu
    // Quan hệ n-n với Color
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_color", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "color_id"))
    private List<Color> colors;

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
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Image> images;

    // Quan hệ n-n với Category
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    // Một sản phẩm có nhiều feedback
    // Quan hệ 1-n với Feedback
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    // Danh sách chi tiết đơn hàng
    // Quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private List<OrderDetail> orderDetails;

    // Quan hệ 1-n với FavoriteProduct
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;

    // Một sản phẩm có thể nằm trong nhiều giỏ hàng
    // Quan hệ 1-n với CartProduct
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartProduct> cartProducts;

    // Các sản phẩm liên quan
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "related_product", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "related_id"))
    private List<Product> relatedProducts;

    // Một sản phẩm có nhiều tính năng
    // Quan hệ n-n với Feature
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "product_feature", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "feature_id"))
    private List<Feature> features;
}
