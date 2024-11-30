package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.OrderStatusDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "orderStatusDetail", path = "orderStatusDetail")
public interface OrderStatusDetailRepository extends JpaRepository<OrderStatusDetail, Long> {

}
