package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
    private LocalDateTime updatedAt; // Thời gian cập nhật
    private String updatedBy; // Tên người cập nhật cuối cùng

    public ProductResponseDto(String name, String description, double price, double salePrice,
            List<String> categorySlugs, List<String> colorNames, List<String> sizeNames,
            List<ProductVariantResponseDto> variants) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.categorySlugs = categorySlugs;
        this.colorNames = colorNames;
        this.sizeNames = sizeNames;
        this.variants = variants;
    }

    public ProductResponseDto(String name, String description, double price, double salePrice,
            List<String> categorySlugs, List<String> colorNames, List<String> sizeNames,
            List<ProductVariantResponseDto> variants, LocalDateTime updatedAt, String updatedBy) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.categorySlugs = categorySlugs;
        this.colorNames = colorNames;
        this.sizeNames = sizeNames;
        this.variants = variants;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }
}
