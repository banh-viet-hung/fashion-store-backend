package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductDto {
    private Long productId;
    private String productName;
    private int totalQuantitySold;
    private double totalRevenue;
    private String imageUrl;
    private int currentStock;
}