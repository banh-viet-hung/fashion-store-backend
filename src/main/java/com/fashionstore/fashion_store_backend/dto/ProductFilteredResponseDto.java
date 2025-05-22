package com.fashionstore.fashion_store_backend.dto;

import com.fashionstore.fashion_store_backend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilteredResponseDto {
    private Long id;
    private String name;
    private double price;
    private double salePrice;
    private double averageRating;
    private String description;
    private String brand;
    private int quantity;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor nhận đối tượng Product
    public ProductFilteredResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.salePrice = product.getSalePrice();
        this.averageRating = product.getAverageRating();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.quantity = product.getQuantity();
        this.deleted = product.isDeleted();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}
