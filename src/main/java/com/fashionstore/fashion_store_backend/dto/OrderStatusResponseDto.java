package com.fashionstore.fashion_store_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusResponseDto {
    private String statusName;
    private String description;
    private LocalDateTime updateAt;
    private String updatedBy;
}
