package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private long totalOrders;
    private double totalRevenue;
    private long totalProducts;
    private long totalCustomers;
    private double averageOrderValue;
    private long pendingOrders;
}