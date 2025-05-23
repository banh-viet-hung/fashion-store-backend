package com.fashionstore.fashion_store_backend.dto;

import com.fashionstore.fashion_store_backend.model.Image;
import com.fashionstore.fashion_store_backend.model.Product;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductFilteredRspDto {
    private Long id;
    private String name;
    private double price;
    private double salePrice;
    private String description;
    private String brand;
    private int quantity;
    private String thumbnailUrl;
    private boolean deleted;
    private LocalDateTime createdAt; // Thời gian tạo sản phẩm
    private LocalDateTime updatedAt; // Thời gian cập nhật lần cuối
    private String updatedBy; // Tên người cập nhật cuối cùng

    public ProductFilteredRspDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.salePrice = product.getSalePrice();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.quantity = product.getQuantity();
        this.deleted = product.isDeleted();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        // Lấy tên người cập nhật nếu có
        this.updatedBy = product.getUpdatedBy() != null ? product.getUpdatedBy().getFullName() : null;
        // Lấy hình ảnh đại diện (ưu tiên thumbnail)
        this.thumbnailUrl = product.getImages().stream()
                .filter(Image::isThumbnail)
                .findFirst()
                .map(Image::getUrl)
                .orElseGet(() -> product.getImages().stream()
                        .findFirst()
                        .map(Image::getUrl)
                        .orElse(null));
    }
}