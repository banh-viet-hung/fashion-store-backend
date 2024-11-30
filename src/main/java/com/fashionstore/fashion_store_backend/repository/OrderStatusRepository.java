package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "orderStatus", path = "orderStatus")
public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {

}
