package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
