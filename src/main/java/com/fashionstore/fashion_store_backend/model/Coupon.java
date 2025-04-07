package com.fashionstore.fashion_store_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private double discountValue;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int usageLimit;
    private int usedCount;
    private double minOrderValue;

    public enum DiscountType {
        FIXED, PERCENT
    }
}
