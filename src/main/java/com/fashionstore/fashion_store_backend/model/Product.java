package com.fashionstore.fashion_store_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private Long id;

    private String name;
    private double price;
    private double salePrice;
    private double averageRating;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String brand;

    private LocalDateTime createdAt;

    // quantity: số lượng sản phẩm còn lại
    private int quantity;

    // updatedAt: thời gian cập nhật sản phẩm
    private LocalDateTime updatedAt;

    // Đã xóa
    private boolean deleted;

    // Quan hệ n-n với Color
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "product_color", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "color_id"))
    private List<Color> colors;

    // Quan hệ n-n với Size
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "product_size", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "size_id"))
    private List<Size> sizes;

    // Một sản phẩm có thể có nhiều ảnh
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Image> images;

    // Quan hệ n-n với Category
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    // Một sản phẩm có thể có nhiều feedback
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    // Quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // Quan hệ 1-n với FavoriteProduct
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;

    // Một sản phẩm có thể nằm trong nhiều giỏ hàng
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartProduct> cartProducts;

    // Các sản phẩm liên quan
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "related_product", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "related_id"))
    private List<Product> relatedProducts;

    // Quan hệ với ProductVariant (mối quan hệ 1-n với ProductVariant)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductVariant> variants;
}
