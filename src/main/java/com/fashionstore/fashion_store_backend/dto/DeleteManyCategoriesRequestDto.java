package com.fashionstore.fashion_store_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteManyCategoriesRequestDto {
    private List<Long> ids;
}
