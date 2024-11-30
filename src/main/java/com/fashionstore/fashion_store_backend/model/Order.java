package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    // ID đơn hàng
    @Id
    private Long id;

    private LocalDateTime orderDate;

    // Địa chỉ giao hàng
    // Quan hệ n-1 với Address
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Address shippingAddress;

    // Tổng tiền
    private double total;

    // Một đơn hàng thuộc một người dùng
    // Quan hệ n-1 với User
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;

    // Hình thức thanh toán
    // Quan hệ n-1 với PaymentMethod
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private PaymentMethod paymentMethod;

    // Hình thức vận chuyển
    // Quan hệ n-1 với ShippingMethod
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private ShippingMethod shippingMethod;

    // Danh sách chi tiết đơn hàng
    // Quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // Một đơn hàng được quản lý bởi một nhân viên
    // Quan hệ n-1 với User
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User staff;

    // Một đơn hàng có nhiều chi tiết trạng thái đơn hàng
    // Quan hệ 1-n với OrderStatusDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderStatusDetail> orderStatusDetails;
}
