package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.ImageService;
import com.fashionstore.fashion_store_backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    // API tạo mới sản phẩm
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductCreateDto productDTO) {
        try {
            // Gọi service để tạo mới sản phẩm
            Long product = productService.createProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo sản phẩm thành công", true, product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API tạo ảnh cho sản phẩm
    @PostMapping("/{productId}/images")
    public ResponseEntity<ApiResponse> createProductImages(@PathVariable Long productId, @RequestBody ProductImagesCreateDto requestDto) {
        try {
            // Gọi service để tạo ảnh
            imageService.createProductImages(productId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo ảnh cho sản phẩm thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/{productId}/images")
    public ResponseEntity<ApiResponse> updateProductImages(@PathVariable Long productId, @RequestBody ProductImagesCreateDto requestDto) {
        try {
            // Gọi service để cập nhật lại hình ảnh
            imageService.updateProductImages(productId, requestDto);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Cập nhật ảnh cho sản phẩm thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }


    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponse> softDeleteProduct(@PathVariable Long productId) {
        try {
            productService.softDeleteProduct(productId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Xóa sản phẩm thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/delete-many")
    public ResponseEntity<ApiResponse> softDeleteManyProducts(@RequestBody DeleteManyProductsRequestDto requestDto) {
        try {
            productService.softDeleteManyProducts(requestDto.getIds());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Xóa nhiều sản phẩm thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            // Gọi service để lấy thông tin sản phẩm
            ProductResponseDto productResponseDto = productService.getProductById(productId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Lấy thông tin sản phẩm thành công", true, productResponseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductCreateDto productDTO) {
        try {
            // Gọi service để cập nhật sản phẩm
            Long updateProductId = productService.updateProduct(productId, productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Cập nhật sản phẩm thành công", true, updateProductId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse> getFilteredProducts(@RequestBody @Valid ProductFilterRequestDto filterRequest) {
        try {
            // Gọi service để lấy danh sách sản phẩm thỏa mãn các điều kiện và sắp xếp
            var productsPage = productService.getFilteredProducts(
                    filterRequest.getCategorySlugs(),
                    filterRequest.getSizeNames(),
                    filterRequest.getColorNames(),
                    filterRequest.getMinPrice(),
                    filterRequest.getMaxPrice(),
                    filterRequest.getPage(),
                    filterRequest.getSize(),
                    filterRequest.getSortBy()  // Thêm tham số sắp xếp
            );

            // Kiểm tra nếu danh sách sản phẩm trống
            if (productsPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Không có sản phẩm phù hợp với điều kiện lọc", true, productsPage));
            }

            // Trả về API response với dữ liệu phân trang khi có sản phẩm
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Lấy sản phẩm thành công", true, productsPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }


    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {
        try {
            // Gọi service để lấy danh sách sản phẩm với phân trang và lọc
            Page<ProductFilteredRspDto> productsPage = productService.getProductList(
                    page, limit, category, title, status);

            // Kiểm tra nếu danh sách sản phẩm trống
            if (productsPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ApiResponse("Không tìm thấy sản phẩm phù hợp", true, productsPage));
            }

            // Trả về phản hồi thành công với dữ liệu phân trang
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Lấy danh sách sản phẩm thành công", true, productsPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

}
