package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.ProductCreateDto;
import com.fashionstore.fashion_store_backend.model.Category;
import com.fashionstore.fashion_store_backend.model.Color;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.Size;
import com.fashionstore.fashion_store_backend.repository.CategoryRepository;
import com.fashionstore.fashion_store_backend.repository.ColorRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import com.fashionstore.fashion_store_backend.repository.SizeRepository;
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
}
