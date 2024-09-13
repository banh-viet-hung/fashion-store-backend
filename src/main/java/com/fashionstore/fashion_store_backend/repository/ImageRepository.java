package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
