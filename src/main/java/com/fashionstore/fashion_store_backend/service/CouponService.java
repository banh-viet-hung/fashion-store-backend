package com.fashionstore.fashion_store_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fashionstore.fashion_store_backend.dto.CouponDto;
import com.fashionstore.fashion_store_backend.dto.CouponFilterRequestDto;
import com.fashionstore.fashion_store_backend.model.Coupon;
import com.fashionstore.fashion_store_backend.repository.CouponRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public double validateCoupon(String code, double orderValue) {
        Coupon coupon = couponRepository.findByCode(code);

        // Check if coupon exists
        if (coupon == null) {
            throw new IllegalArgumentException("Mã giảm giá không tồn tại");
        }

        // Check if coupon is active based on start and end date
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn hoặc chưa có hiệu lực");
        }

        // Check if coupon has reached usage limit
        if (coupon.getUsageLimit() > 0 && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng");
        }

        // Check minimum order value
        if (orderValue < coupon.getMinOrderValue()) {
            throw new IllegalArgumentException(
                    "Giá trị đơn hàng không đủ để áp dụng mã giảm giá. Tối thiểu: " + coupon.getMinOrderValue());
        }

        // Calculate discount amount
        double discountAmount = 0;
        if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
            discountAmount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
            discountAmount = orderValue * (coupon.getDiscountValue() / 100);
        }

        // Make sure discount amount doesn't exceed order value
        if (discountAmount > orderValue) {
            discountAmount = orderValue;
        }

        return discountAmount;
    }
    
    // Create a new coupon
    public Coupon createCoupon(CouponDto couponDto) {
        // Check if coupon code already exists
        Coupon existingCoupon = couponRepository.findByCode(couponDto.getCode());
        if (existingCoupon != null) {
            throw new IllegalArgumentException("Mã giảm giá này đã tồn tại");
        }
        
        // Validate dates
        if (couponDto.getEndDate().isBefore(couponDto.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
        
        // Create new coupon from DTO
        Coupon newCoupon = new Coupon();
        newCoupon.setCode(couponDto.getCode());
        newCoupon.setDescription(couponDto.getDescription());
        newCoupon.setDiscountType(couponDto.getDiscountType());
        newCoupon.setDiscountValue(couponDto.getDiscountValue());
        newCoupon.setStartDate(couponDto.getStartDate());
        newCoupon.setEndDate(couponDto.getEndDate());
        newCoupon.setUsageLimit(couponDto.getUsageLimit() != null ? couponDto.getUsageLimit() : 0);
        newCoupon.setUsedCount(0); // Initialize used count as 0
        newCoupon.setMinOrderValue(couponDto.getMinOrderValue() != null ? couponDto.getMinOrderValue() : 0);
        
        return couponRepository.save(newCoupon);
    }
    
    // Update an existing coupon
    public Coupon updateCoupon(Long id, CouponDto couponDto) {
        // Check if coupon exists
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));
        
        // If code is changed, check if new code already exists
        if (!existingCoupon.getCode().equals(couponDto.getCode())) {
            Coupon codeCheck = couponRepository.findByCode(couponDto.getCode());
            if (codeCheck != null) {
                throw new IllegalArgumentException("Mã giảm giá này đã tồn tại");
            }
        }
        
        // Validate dates
        if (couponDto.getEndDate().isBefore(couponDto.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
        
        // Update coupon
        existingCoupon.setCode(couponDto.getCode());
        existingCoupon.setDescription(couponDto.getDescription());
        existingCoupon.setDiscountType(couponDto.getDiscountType());
        existingCoupon.setDiscountValue(couponDto.getDiscountValue());
        existingCoupon.setStartDate(couponDto.getStartDate());
        existingCoupon.setEndDate(couponDto.getEndDate());
        existingCoupon.setUsageLimit(couponDto.getUsageLimit() != null ? couponDto.getUsageLimit() : 0);
        existingCoupon.setMinOrderValue(couponDto.getMinOrderValue() != null ? couponDto.getMinOrderValue() : 0);
        
        return couponRepository.save(existingCoupon);
    }
    
    // Get coupon by ID
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));
    }
    
    // Get all coupons
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    // Get coupons with pagination and filtering
    public Page<Coupon> getCouponsWithPagination(int page, int size, CouponFilterRequestDto filterDto) {
        // Tạo Pageable với phân trang và sắp xếp theo ID giảm dần
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("id")));
        
        // Tạo Specification để áp dụng điều kiện lọc
        Specification<Coupon> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Lọc theo mã giảm giá nếu có
            if (filterDto.getCode() != null && !filterDto.getCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("code")), 
                    "%" + filterDto.getCode().toLowerCase() + "%"
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // Lấy danh sách mã giảm giá từ database dựa trên các điều kiện lọc
        return couponRepository.findAll(specification, pageable);
    }
    
    // Xóa một coupon
    public void deleteCoupon(Long id) {
        // Kiểm tra xem coupon có tồn tại không
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));
        
        // Xóa coupon
        couponRepository.delete(coupon);
    }
    
    // Xóa nhiều coupon
    @Transactional
    public void deleteManyCoupons(List<Long> ids) {
        for (Long id : ids) {
            // Kiểm tra xem coupon có tồn tại không
            Coupon coupon = couponRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá với ID: " + id));
            
            // Xóa coupon
            couponRepository.delete(coupon);
        }
    }
}