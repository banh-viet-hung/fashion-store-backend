package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "orderDetail", path = "orderDetail")
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
