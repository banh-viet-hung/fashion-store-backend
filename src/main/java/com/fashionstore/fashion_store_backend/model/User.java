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

    private String resetToken;
    private LocalDateTime resetTokenExpiration;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Feedback> feedback;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    // Chỉ định một Role duy nhất cho User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id") // Khóa ngoại trong bảng User
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FavoriteProduct> favoriteProducts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CartProduct> cartProducts;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    private List<Order> staffOrders;
}
