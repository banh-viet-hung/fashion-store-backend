package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "shippingMethod", path = "shippingMethod")
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    // Tìm phương thức vận chuyển theo mã, trả về Optional để sử dụng orElseThrow
    Optional<ShippingMethod> findByCode(String code);
}
