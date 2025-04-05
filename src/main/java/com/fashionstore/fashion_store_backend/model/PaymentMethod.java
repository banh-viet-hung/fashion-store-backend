package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String description;

    // Một hình thức thanh toán có thể áp dụng cho nhiều đơn hàng
    // Quan hệ 1-n với Order
    // mappedBy trỏ tới tên biến paymentMethod ở trong class Order
    @OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> order;
}
