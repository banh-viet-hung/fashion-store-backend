package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "product", path = "product")
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // Tìm sản phẩm theo tên và phân trang
    // http://localhost:8080/product/search/findByNameContainingIgnoreCase?name=Áo%20thun%20thể%20thao%20Jacquard%20Seamless&page=0&size=10
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm sản phẩm chưa bị xóa (deleted = false) và phân trang
    // http://localhost:8080/product/search/findByDeletedFalse?page=0&size=10
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    Page<Product> findByDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.categories c " +
            "LEFT JOIN p.sizes s " +
            "LEFT JOIN p.colors col " +
            "WHERE (:categorySlugs IS NULL OR c.slug IN :categorySlugs) " +
            "AND (:sizeNames IS NULL OR s.name IN :sizeNames) " +
            "AND (:colorNames IS NULL OR col.name IN :colorNames) " +
            "AND (:minPrice IS NULL OR (p.salePrice >= :minPrice OR (p.salePrice = 0 AND p.price >= :minPrice))) " +
            "AND (:maxPrice IS NULL OR (p.salePrice <= :maxPrice OR (p.salePrice = 0 AND p.price <= :maxPrice))) " +
            "GROUP BY p.id " +
            "HAVING (:categorySlugs IS NULL OR COUNT(DISTINCT c.id) >= :categorySlugsCount) " +
            "AND (:colorNames IS NULL OR COUNT(DISTINCT col.id) >= :colorNamesCount) " +
            "AND (:sizeNames IS NULL OR COUNT(DISTINCT s.id) >= :sizeNamesCount)")
    Page<Product> findFilteredProducts(
            List<String> categorySlugs,
            List<String> sizeNames,
            List<String> colorNames,
            Double minPrice,
            Double maxPrice,
            Pageable pageable,
            @Param("categorySlugsCount") int categorySlugsCount,
            @Param("colorNamesCount") int colorNamesCount,
            @Param("sizeNamesCount") int sizeNamesCount);



}
