package com.fashionstore.fashion_store_backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsDto {
    private LocalDate date;
    private String period; // Có thể là ngày, tuần, tháng, năm
    private double revenue;
    private long orderCount;
}