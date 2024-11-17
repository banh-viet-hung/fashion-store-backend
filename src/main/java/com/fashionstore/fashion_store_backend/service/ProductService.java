package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Tìm sản phẩm theo ID
    public Product findById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
