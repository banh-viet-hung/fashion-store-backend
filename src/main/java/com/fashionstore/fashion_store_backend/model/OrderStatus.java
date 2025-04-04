package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class OrderStatus {
    
    @Id
    private String code;

    private String statusName;

    // Mô tả trạng thái
    private String description;

    // Một trạng thái đơn hàng có nhiều chi tiết trạng thái đơn hàng
    // Quan hệ 1-n với OrderStatusDetail
    @OneToMany(mappedBy = "orderStatus", cascade = CascadeType.ALL)
    private List<OrderStatusDetail> orderStatusDetails;
}
