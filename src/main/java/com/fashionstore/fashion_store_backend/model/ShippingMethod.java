package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    private String code;

    private String name;

    private String description;

    private double fee;

    // Quan hệ 1-n với Order
    // mappedBy trỏ tới tên biến shippingMethod ở trong class Order
    @OneToMany(mappedBy = "shippingMethod", cascade = CascadeType.ALL)
    private List<Order> order;

}
