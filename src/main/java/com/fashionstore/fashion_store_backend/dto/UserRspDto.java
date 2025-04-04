package com.fashionstore.fashion_store_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRspDto {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String avatar;
    private boolean isActive;
    private String roleName;
}