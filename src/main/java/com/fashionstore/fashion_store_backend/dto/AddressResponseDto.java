package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {
    private Long id;              // ID của địa chỉ
    private String fullName;      // Tên đầy đủ của người nhận
    private String phoneNumber;    // Số điện thoại của người nhận
    private String address;       // Địa chỉ cụ thể
    private String city;          // Thành phố
    private String district;       // Quận/huyện
    private String ward;          // Phường/xã
    private boolean defaultAddress; // Có phải là địa chỉ mặc định không
}
