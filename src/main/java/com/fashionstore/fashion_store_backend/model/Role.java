package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Một role có thể áp dụng cho nhiều người dùng
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> users; // Không cần thay đổi gì ở đây
}
