package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "order", path = "order")
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    public List<Order> findByUser_Email(String email);

    /**
     * Tìm danh sách đơn hàng trong khoảng thời gian
     * 
     * @param startDate Thời gian bắt đầu
     * @param endDate   Thời gian kết thúc
     * @return Danh sách đơn hàng
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
