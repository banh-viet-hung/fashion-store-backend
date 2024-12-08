package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Image;
import com.fashionstore.fashion_store_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "image", path = "image")
public interface ImageRepository extends JpaRepository<Image, Long> {
    // Tìm tất cả hình ảnh của một sản phẩm
    List<Image> findByProduct(Product product);
}
