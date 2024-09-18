package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "color", path = "color")
public interface ColorRepository extends JpaRepository<Color, Long> {
}
