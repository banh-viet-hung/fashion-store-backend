package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderResponseDto {
    private Long id; // Mã đơn hàng
    private LocalDateTime orderDate; // Ngày đặt hàng
    private String phoneNumber; // Số điện thoại người nhận
    private double total; // Tổng tiền
    private String currentStatus; // Trạng thái hiện tại của đơn hàng
} 