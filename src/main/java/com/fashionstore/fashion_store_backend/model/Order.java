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

    // Địa chỉ giao hàng
    // Quan hệ n-1 với Address
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
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
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;

    // Hình thức thanh toán
    // Quan hệ n-1 với PaymentMethod
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private PaymentMethod paymentMethod;

    // Hình thức vận chuyển
    // Quan hệ n-1 với ShippingMethod
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private ShippingMethod shippingMethod;

    // Danh sách chi tiết đơn hàng
    // Quan hệ 1-n với OrderDetail
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails;
}
