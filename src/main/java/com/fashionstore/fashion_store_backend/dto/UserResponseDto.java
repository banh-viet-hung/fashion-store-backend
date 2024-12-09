package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String fullName;
    private String avatar;
    private String email;
    private String phoneNumber;
    private String roleName;
    private boolean active;
}
