package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OrderStatusDetail {

    @Id
    @GeneratedValue
    private Long id;

    // Một chi tiết trạng thái đơn hàng tương ứng với một OrderStatus
    // Quan hệ n-1 với OrderStatus
    @ManyToOne
    private OrderStatus orderStatus;

    // Một chi tiết trạng thái đơn hàng tương ứng với một đơn hàng
    // Quan hệ n-1 với Order
    @ManyToOne
    private Order order;

    // Thời gian cập nhật trạng thái
    private LocalDateTime updateAt;

    // Chi tiết trạng thái này được cập nhật bởi một nhân viên hoặc một người dùng
    // Quan hệ n-1 với User
    @ManyToOne
    private User user;

    // Trạng thái đang hoạt động hay không
    private boolean isActive;

    // Lý do hủy đơn hàng (chỉ có giá trị khi trạng thái là CANCELLED)
    private String cancelReason;
}
