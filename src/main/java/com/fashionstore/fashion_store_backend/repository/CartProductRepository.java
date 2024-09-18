package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "cartProduct", path = "cartProduct")
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
}
