package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String avatar;

    private String resetToken; // Token cho việc đặt lại mật khẩu
    private LocalDateTime resetTokenExpiration; // Thời gian hết hạn của token

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Feedback> feedback;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CartProduct> cartProducts;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    private List<Order> staffOrders;
}
