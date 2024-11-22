package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
