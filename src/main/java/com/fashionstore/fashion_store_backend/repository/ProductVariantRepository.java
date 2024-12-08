package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Color;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.ProductVariant;
import com.fashionstore.fashion_store_backend.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "product_variant", path = "product_variant")
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    ProductVariant findByProductIdAndColorAndSize(Long productId, Color color, Size size);

    // Tìm tất cả ProductVariant thuộc một sản phẩm
    List<ProductVariant> findByProduct(Product product);
}

