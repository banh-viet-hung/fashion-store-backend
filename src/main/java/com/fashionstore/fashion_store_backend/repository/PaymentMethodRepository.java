package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "paymentMethod", path = "paymentMethod")
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}
