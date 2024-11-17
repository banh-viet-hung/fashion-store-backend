package com.fashionstore.fashion_store_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khóa ngoại tới Product
    @ManyToOne
    private Product product;

    // Khóa ngoại tới Color
    @ManyToOne
    private Color color;

    // Khóa ngoại tới Size
    @ManyToOne
    private Size size;

    // Số lượng sản phẩm của sự kết hợp này
    private int quantity;

}
