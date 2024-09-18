package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // fullName
    private String fullName;

    // phoneNumber
    private String phoneNumber;

    // address
    private String address;

    // city
    private String city;

    // district
    private String district;

    // phường, xã
    private String ward;

    // Loại địa chỉ
    private String type;

    // Một địa chỉ thuộc một người dùng
    // Quan hệ n-1 với User
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private User user;

    // Một địa chỉ có thể thuộc nhiều đơn hàng
    // Quan hệ 1-n với Order
    @OneToMany(mappedBy = "shippingAddress", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Order> orders;
}
