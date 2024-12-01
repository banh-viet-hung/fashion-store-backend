package com.fashionstore.fashion_store_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data

public class OrderItemResponseDto {
    private Long productId;
    private String size;
    private String color;
    private Integer quantity;
    private double price;
}
