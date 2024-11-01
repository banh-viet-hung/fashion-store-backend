package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private String fullName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
}
