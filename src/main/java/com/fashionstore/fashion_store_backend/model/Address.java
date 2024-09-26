package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String address;

    private String city;

    private String district;

    // phường, xã
    private String ward;

    // Loại địa chỉ
    private String type;

    // Một địa chỉ thuộc một người dùng
    // Quan hệ n-1 với User
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;

    // Một địa chỉ có thể thuộc nhiều đơn hàng
    // Quan hệ 1-n với Order
    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL)
    private List<Order> orders;
}
