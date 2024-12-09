package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.CategoryCreateDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // API tạo mới danh mục
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryCreateDto categoryCreateDto) {
        try {
            // Gọi service để tạo mới danh mục
            Long categoryId = categoryService.createCategory(categoryCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo danh mục thành công", true, categoryId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API cập nhật danh mục theo ID
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryCreateDto categoryCreateDto) {
        try {
            // Gọi service để cập nhật danh mục
            categoryService.updateCategory(id, categoryCreateDto);
            return ResponseEntity.ok(new ApiResponse("Cập nhật danh mục thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API xóa danh mục theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("id") Long id) {
        try {
            // Gọi service để xóa danh mục
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse("Xóa danh mục thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }
}
