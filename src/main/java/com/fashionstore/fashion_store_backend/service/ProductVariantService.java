package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.ProductVariantsCreateRequestDto;
import com.fashionstore.fashion_store_backend.model.Color;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.ProductVariant;
import com.fashionstore.fashion_store_backend.model.Size;
import com.fashionstore.fashion_store_backend.repository.ColorRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
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

    @Autowired
    private ProductRepository productRepository;

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

    public void createProductVariants(ProductVariantsCreateRequestDto requestDto) throws Exception {
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));

        int totalQuantity = 0; // Biến để cộng dồn số lượng

        for (var variantDto : requestDto.getVariants()) {
            // Tìm màu và kích thước từ tên
            Color color = colorRepository.findByName(variantDto.getColorName());
            Size size = sizeRepository.findByName(variantDto.getSizeName());

            // Tạo mới ProductVariant
            ProductVariant productVariant = new ProductVariant();
            productVariant.setProduct(product);
            productVariant.setColor(color);
            productVariant.setSize(size);
            productVariant.setQuantity(variantDto.getQuantity());

            // Cộng dồn số lượng vào tổng số lượng của sản phẩm
            totalQuantity += variantDto.getQuantity();

            // Lưu vào cơ sở dữ liệu
            productVariantRepository.save(productVariant);
        }

        // Cập nhật lại quantity của sản phẩm
        product.setQuantity(totalQuantity);

        // Lưu sản phẩm với số lượng mới
        productRepository.save(product);
    }
}
