package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private String name;
    private String description;
    private double price;
    private double salePrice;
    private List<String> categorySlugs;
    private List<String> colorNames;
    private List<String> sizeNames;
    private List<ProductVariantResponseDto> variants; // Mảng chứa các variant của sản phẩm
}
