package com.fashionstore.fashion_store_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fashionstore.fashion_store_backend.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {
    Coupon findByCode(String code);
}
