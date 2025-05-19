package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusStatisticsDto {
    private String statusCode;
    private String statusName;
    private long count;
    private double percentage;
}