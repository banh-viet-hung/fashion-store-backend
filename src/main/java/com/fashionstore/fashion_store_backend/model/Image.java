package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Là thumbnail hay không
    private boolean isThumbnail;

    // Cách 1: Lưu ảnh dưới dạng URL (lưu ảnh trên server)
    private String url;

    // Cách 2: Lưu ảnh dưới dạng base64 (lưu ảnh trực tiếp trong database)
    @Lob
    private String base64;

    // Văn bản thay thế cho ảnh
    private String altText;

    // ngày tạo ảnh
    private LocalDateTime createdAt;

    // ngày cập nhật ảnh
    private LocalDateTime updatedAt;

    // Quan hệ n-1 với Product
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;


}
