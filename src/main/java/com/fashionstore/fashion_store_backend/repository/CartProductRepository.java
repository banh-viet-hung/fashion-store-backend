package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.CartProduct;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "cartProduct", path = "cartProduct")
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    Optional<CartProduct> findByUserAndProductAndSizeNameAndColorName(User user, Product product, String sizeName, String colorName);

    List<CartProduct> findByUser(User user);
}
