package com.fashionstore.fashion_store_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProductImagesCreateDto {

    // Danh sách URL của ảnh
    @NotEmpty(message = "Danh sách ảnh không được để trống")
    @Size(min = 1, max = 6, message = "Danh sách ảnh phải có ít nhất 1 ảnh và tối đa 6 ảnh")
    private List<String> imageUrls;
}
