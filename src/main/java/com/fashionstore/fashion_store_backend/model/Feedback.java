package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Đánh giá từ 1 đến 5
    private int rating;

    // Bình luận
    private String comment;

    // ngày tạo feedback
    private LocalDateTime createdAt;

    // ngày cập nhật feedback
    private LocalDateTime updatedAt;

    // Một feedback thuộc một sản phẩm
    // Quan hệ n-1 với Product
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;

    // Một feedback thuộc một người dùng
    // Quan hệ n-1 với User
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;
}
