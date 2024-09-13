package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "favoriteProduct", path = "favoriteProduct")
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
}
