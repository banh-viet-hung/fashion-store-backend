package com.fashionstore.fashion_store_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteManyCouponsRequestDto {
    private List<Long> ids;
} 