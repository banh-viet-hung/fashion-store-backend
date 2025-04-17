package com.fashionstore.fashion_store_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fashionstore.fashion_store_backend.dto.CouponDto;
import com.fashionstore.fashion_store_backend.dto.CouponFilterRequestDto;
import com.fashionstore.fashion_store_backend.dto.CouponValidationRequestDto;
import com.fashionstore.fashion_store_backend.dto.DeleteManyCouponsRequestDto;
import com.fashionstore.fashion_store_backend.model.Coupon;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.CouponService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateCoupon(@RequestBody CouponValidationRequestDto requestDto) {
        try {
            double discountAmount = couponService.validateCoupon(requestDto.getCode(), requestDto.getOrderValue());
            
            // Create response object with discount amount
            return ResponseEntity.ok(new ApiResponse("Mã giảm giá hợp lệ", true, discountAmount));
        } catch (IllegalArgumentException e) {
            // Return error message when coupon is invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi xác thực mã giảm giá", false));
        }
    }
    
    // API to get all coupons with pagination and filtering (for ADMIN and STAFF)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllCoupons(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String code) {
        try {
            // Tạo đối tượng DTO để truyền các tham số lọc
            CouponFilterRequestDto filterDto = new CouponFilterRequestDto(code);
            
            // Gọi service để lấy danh sách mã giảm giá theo các điều kiện lọc và phân trang
            Page<Coupon> couponPage = couponService.getCouponsWithPagination(page, size, filterDto);
            
            // Trả về kết quả
            return ResponseEntity.ok(new ApiResponse("Lấy danh sách mã giảm giá thành công", true, couponPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi lấy danh sách mã giảm giá", false));
        }
    }
    
    // API to get coupon by ID (for ADMIN and STAFF)
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse> getCouponById(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getCouponById(id);
            return ResponseEntity.ok(new ApiResponse("Lấy thông tin mã giảm giá thành công", true, coupon));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi lấy thông tin mã giảm giá", false));
        }
    }
    
    // API to create a new coupon (for ADMIN and STAFF)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCoupon(@Valid @RequestBody CouponDto couponDto) {
        try {
            Coupon newCoupon = couponService.createCoupon(couponDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Tạo mã giảm giá mới thành công", true, newCoupon));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi tạo mã giảm giá", false));
        }
    }
    
    // API to update an existing coupon (for ADMIN and STAFF)
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponDto couponDto) {
        try {
            Coupon updatedCoupon = couponService.updateCoupon(id, couponDto);
            return ResponseEntity.ok(new ApiResponse("Cập nhật mã giảm giá thành công", true, updatedCoupon));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi cập nhật mã giảm giá", false));
        }
    }
    
    // API xóa một coupon theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCoupon(@PathVariable Long id) {
        try {
            couponService.deleteCoupon(id);
            return ResponseEntity.ok(new ApiResponse("Xóa mã giảm giá thành công", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi xóa mã giảm giá", false));
        }
    }
    
    // API xóa nhiều coupon
    @PostMapping("/delete-many")
    public ResponseEntity<ApiResponse> deleteManyCoupons(@RequestBody DeleteManyCouponsRequestDto request) {
        try {
            couponService.deleteManyCoupons(request.getIds());
            return ResponseEntity.ok(new ApiResponse("Xóa các mã giảm giá thành công", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi xóa các mã giảm giá", false));
        }
    }
} 