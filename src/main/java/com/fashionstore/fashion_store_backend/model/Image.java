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

    // Dữ liệu ảnh
    private String url;

    // Văn bản thay thế cho ảnh
    private String altText;

    // Quan hệ n-1 với Product
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Product product;

}
