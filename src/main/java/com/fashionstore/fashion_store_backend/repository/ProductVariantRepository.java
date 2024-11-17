package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "product_variant", path = "product_variant")
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}
