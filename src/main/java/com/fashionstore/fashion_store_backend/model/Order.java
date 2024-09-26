package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "orders")
public class Order {

    // ID đơn hàng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ngày đặt hàng
    private LocalDateTime orderDate;

    // Trạng thái đơn hàng
    private String status;

    // Địa chỉ giao hàng
    // Quan hệ n-1 với Address
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private Address shippingAddress;

    // Tổng tiền sản phẩm
    private double totalProductPrice;

    // Chi phí vận chuyển
    private double shippingFee;

    // Thuế
    private double tax;

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
    private Set<OrderDetail> orderDetails;

    // Một đơn hàng được quản lý bởi một nhân viên
    // Quan hệ n-1 với User
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User staff;
}
