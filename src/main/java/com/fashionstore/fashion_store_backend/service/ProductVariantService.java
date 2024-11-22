package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.model.Color;
import com.fashionstore.fashion_store_backend.model.ProductVariant;
import com.fashionstore.fashion_store_backend.model.Size;
import com.fashionstore.fashion_store_backend.repository.ColorRepository;
import com.fashionstore.fashion_store_backend.repository.ProductVariantRepository;
import com.fashionstore.fashion_store_backend.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    public int getProductQuantity(Long productId, String colorName, String sizeName) {
        // Tìm kiếm màu sắc và kích thước nếu có
        Color color = null;
        Size size = null;

        if (colorName != null && !colorName.isEmpty()) {
            color = colorRepository.findByName(colorName); // Tìm kiếm màu sắc theo tên
        }

        if (sizeName != null && !sizeName.isEmpty()) {
            size = sizeRepository.findByName(sizeName); // Tìm kiếm kích thước theo tên
        }

        // Tìm kiếm bản ghi trong product_variant theo các điều kiện
        ProductVariant productVariant = productVariantRepository.findByProductIdAndColorAndSize(productId, color, size);

        // Nếu không tìm thấy bản ghi, trả về 0
        return productVariant != null ? productVariant.getQuantity() : 0;
    }
}
