package com.fashionstore.fashion_store_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductVariantsCreateRequestDto {
    private Long productId;
    private List<ProductVariantCreateDto> variants;
}
