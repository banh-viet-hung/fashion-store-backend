package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatisticsDto {
    private Long productId;
    private String productName;
    private int quantity;
    private String imageUrl;
    private double price;
    private int lowStockThreshold = 10;
    private boolean isLowStock;
}