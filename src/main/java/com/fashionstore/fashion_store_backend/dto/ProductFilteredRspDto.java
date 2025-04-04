package com.fashionstore.fashion_store_backend.dto;

import com.fashionstore.fashion_store_backend.model.Image;
import com.fashionstore.fashion_store_backend.model.Product;
import lombok.Data;

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

    public ProductFilteredRspDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.salePrice = product.getSalePrice();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.quantity = product.getQuantity();

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