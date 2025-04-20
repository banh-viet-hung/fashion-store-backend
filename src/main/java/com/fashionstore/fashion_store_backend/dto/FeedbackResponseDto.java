package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int rating;
    private boolean isPublic;
    private boolean isEdited;
    private String color;
    private String size;
    
    // Thông tin sản phẩm
    private String productImage;
    private Long productId;
    private String productName;
    
    // Thông tin người dùng
    private String userEmail;
    private String userName;
    private String userAvatar;
} 