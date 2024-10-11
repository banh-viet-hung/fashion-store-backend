package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // fullName
    private String fullName;

    // phoneNumber
    private String phoneNumber;

    // gender
    private String gender;

    // dateOfBirth
    private LocalDate dateOfBirth;

    // chiều cao
    private int height;

    // cân nặng
    private int weight;

    // email đăng nhập
    private String email;

    private String password;

    // Avatar
    private String avatar;

    // Một người dùng có thể có nhiều địa chỉ
    // Quan hệ 1-n với Address
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    // Một người dùng có thể tạo nhiều feedback
    // Quan hệ 1-n với Feedback
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Feedback> feedback;

    // Một người dùng có thể tạo nhiều đơn hàng
    // Quan hệ 1-n với Order
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    // Một người dùng có thể có nhiều vai trò
    // Quan hệ n-n với Role
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    // Danh sách sản phẩm yêu thích của người dùng
    // Quan hệ 1-n với FavoriteProduct
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;

    // Một người dùng có thể có nhiều sản phẩm trong giỏ hàng
    // Quan hệ 1-n với CartProduct
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CartProduct> cartProducts;

    // Một nhân viên quản lý nhiều đơn hàng
    // Quan hệ 1-n với Order
    @OneToMany(mappedBy = "staff",  cascade = CascadeType.ALL)
    private List<Order> staffOrders;
}
