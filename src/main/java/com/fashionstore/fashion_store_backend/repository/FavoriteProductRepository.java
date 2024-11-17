package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.FavoriteProduct;
import com.fashionstore.fashion_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "favoriteProduct", path = "favoriteProduct")
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    @Query("SELECT fp FROM FavoriteProduct fp WHERE fp.user = :user AND fp.product.id = :productId")
    Optional<FavoriteProduct> findByUserAndProductId(@Param("user") User user, @Param("productId") Long productId);
}
