package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Category;
import com.fashionstore.fashion_store_backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "category", path = "category")
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Tìm kiếm danh mục theo slug
    @Query("SELECT c FROM Category c WHERE c.slug = :slug")
    Optional<Category> findBySlug(@Param("slug") String slug);

    // Tìm các sản phẩm trong danh mục theo slug với phân trang
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.slug = :slug")
    Page<Product> findProductsByCategorySlug(@Param("slug") String slug, Pageable pageable);

    // Thêm phương thức tìm danh mục theo danh sách slug
    List<Category> findBySlugIn(List<String> slugs);

    // Tìm kiếm danh mục theo trạng thái deleted
    List<Category> findByDeleted(boolean deleted);
}
