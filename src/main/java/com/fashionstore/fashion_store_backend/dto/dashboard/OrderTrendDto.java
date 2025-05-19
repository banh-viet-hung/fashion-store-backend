package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrendDto {
    private LocalDate date;
    private String period;
    private long orderCount;
    private double growth; // Phần trăm tăng trưởng so với kỳ trước
}