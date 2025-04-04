package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.CategoryCreateDto;
import com.fashionstore.fashion_store_backend.dto.CategoryResponseDto;
import com.fashionstore.fashion_store_backend.dto.DeleteManyCategoriesRequestDto;
import com.fashionstore.fashion_store_backend.model.Category;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Tạo danh mục thành công", true, categoryId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
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

    @PostMapping("/delete-many")
    public ResponseEntity<ApiResponse> deleteManyCategories(@RequestBody DeleteManyCategoriesRequestDto request) {
        try {
            categoryService.deleteManyCategories(request.getIds());
            return ResponseEntity.ok(new ApiResponse("Xóa các danh mục thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }


    @GetMapping("/children/{slug}")
    public ResponseEntity<ApiResponse> getChildCategoriesBySlug(@PathVariable("slug") String slug) {
        try {
            // Gọi service để lấy danh sách các danh mục con
            List<Category> childCategories = categoryService.getChildCategoriesBySlug(slug);

            // Chuyển đổi danh sách Category thành danh sách CategoryResponseDto
            List<CategoryResponseDto> responseDtos = childCategories.stream()
                    .map(category -> new CategoryResponseDto(category.getId(), category.getName(), category.getSlug()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse("Lấy danh sách danh mục con thành công", true, responseDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

}
