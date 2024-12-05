package com.fashionstore.fashion_store_backend.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductVariantCreateDto {

    private String colorName;

    private String sizeName;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;
}