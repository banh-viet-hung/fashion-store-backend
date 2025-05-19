package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySalesDto {
    private Long categoryId;
    private String categoryName;
    private double totalRevenue;
    private long orderCount;
    private int itemCount;
    private double percentage;
}