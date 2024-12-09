package com.fashionstore.fashion_store_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreateDto {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 3, max = 50, message = "Tên danh mục phải có ít nhất 3 ký tự và tối đa 50 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(min = 3, max = 50, message = "Slug phải có ít nhất 3 ký tự và tối đa 50 ký tự")
    private String slug;
}
