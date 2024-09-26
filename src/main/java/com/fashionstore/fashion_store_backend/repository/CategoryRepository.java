package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "category", path = "category")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tìm kếm một danh mục theo slug
//    http://localhost:8080/category/search/findBySlug?slug=ao-thun
    @Query("SELECT c FROM Category c WHERE c.slug = :slug")
    Optional<Category> findBySlug(@Param("slug") String slug);
}
