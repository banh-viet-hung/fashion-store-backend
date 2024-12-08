package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.ProductVariantsCreateRequestDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-variant")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;

    @GetMapping("/quantity")
    public ResponseEntity<ApiResponse> getProductQuantity(
            @RequestParam Long productId,
            @RequestParam(required = false) String colorName,
            @RequestParam(required = false) String sizeName) {

        // Gọi service để lấy số lượng sản phẩm
        int quantity = productVariantService.getProductQuantity(productId, colorName, sizeName);

        // Trả về kết quả
        if (quantity == 0) {
            return ResponseEntity.ok(new ApiResponse("Không tìm thấy sản phẩm với các điều kiện này", true, quantity));
        }
        return ResponseEntity.ok(new ApiResponse("Số lượng sản phẩm", true, quantity));
    }

    // API tạo mới ProductVariant cho sản phẩm
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse> createProductVariants(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantsCreateRequestDto requestDto) {
        try {
            requestDto.setProductId(productId);
            // Gọi service để tạo ProductVariants
            productVariantService.createProductVariants(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo ProductVariant thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/{productId}/variants")
    public ResponseEntity<ApiResponse> updateProductVariants(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantsCreateRequestDto requestDto) {
        try {
            requestDto.setProductId(productId);
            // Gọi service để cập nhật lại ProductVariants
            productVariantService.updateProductVariants(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Cập nhật ProductVariant thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

}
