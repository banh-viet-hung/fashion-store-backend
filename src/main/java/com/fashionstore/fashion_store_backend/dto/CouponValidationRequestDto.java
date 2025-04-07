package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationRequestDto {
    private String code;
    private double orderValue;
} 