package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
}
