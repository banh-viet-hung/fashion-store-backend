package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Order;
import com.fashionstore.fashion_store_backend.model.OrderStatusDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "orderStatusDetail", path = "orderStatusDetail")
public interface OrderStatusDetailRepository extends JpaRepository<OrderStatusDetail, Long> {
    OrderStatusDetail findTopByOrderAndIsActiveTrueOrderByUpdateAtDesc(Order order);
    
    // Tìm tất cả các trạng thái đang active của một đơn hàng
    List<OrderStatusDetail> findByOrderAndIsActiveTrue(Order order);
}
