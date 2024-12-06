package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "product", path = "product")
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm sản phẩm theo tên và phân trang
    // http://localhost:8080/product/search/findByNameContainingIgnoreCase?name=Áo%20thun%20thể%20thao%20Jacquard%20Seamless&page=0&size=10
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm sản phẩm chưa bị xóa (deleted = false) và phân trang
    // http://localhost:8080/product/search/findByDeletedFalse?page=0&size=10
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findByDeletedFalse(Pageable pageable);
}
