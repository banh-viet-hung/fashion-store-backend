package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderFilterRequestDto {
    private Long orderId;
    private String orderStatusCode;
    private String paymentMethodCode;
    private String shippingMethodCode;
    private LocalDate startDate;
    private LocalDate endDate;
} 