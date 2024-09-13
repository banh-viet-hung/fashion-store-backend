package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "order", path = "order")
public interface OrderRepository extends JpaRepository<Order, Long> {
}
