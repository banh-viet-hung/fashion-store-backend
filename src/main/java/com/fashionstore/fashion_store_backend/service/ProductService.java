package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.ProductCreateDto;
import com.fashionstore.fashion_store_backend.model.*;
import com.fashionstore.fashion_store_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;
    ;
    // Tìm sản phẩm theo ID
    public Product findById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    // Hàm tạo mới sản phẩm
    public Long createProduct(ProductCreateDto productDTO) throws Exception {
        // Tạo sản phẩm mới
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSalePrice(productDTO.getSalePrice() > 0 ? productDTO.getSalePrice() : 0);
        product.setCreatedAt(LocalDateTime.now());
        product.setDeleted(false);

        // Lấy danh sách Category từ slug
        List<Category> categories = categoryRepository.findBySlugIn(productDTO.getCategorySlugs());
        if (categories.isEmpty()) {
            throw new Exception("Danh mục không tồn tại");
        }
        product.setCategories(categories);

        // Lấy danh sách Color từ tên màu
        List<Color> colors = colorRepository.findByNameIn(productDTO.getColorNames());
        product.setColors(colors);

        // Lấy danh sách Size từ tên kích thước
        List<Size> sizes = sizeRepository.findByNameIn(productDTO.getSizeNames());
        product.setSizes(sizes);

        // Lưu sản phẩm vào cơ sở dữ liệu
        return productRepository.save(product).getId();
    }

    @Transactional
    public void softDeleteProduct(Long productId) {
        // Lấy sản phẩm theo ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        // Cập nhật số lượng của sản phẩm về 0
        product.setQuantity(0);

        // Cập nhật số lượng của tất cả các biến thể của sản phẩm về 0
        for (ProductVariant variant : product.getVariants()) {
            variant.setQuantity(0);
            productVariantRepository.save(variant); // Lưu lại biến thể sau khi thay đổi số lượng
        }

        // Đánh dấu sản phẩm là đã xóa
        product.setDeleted(true);

        // Lưu sản phẩm sau khi thay đổi
        productRepository.save(product);
    }
}
