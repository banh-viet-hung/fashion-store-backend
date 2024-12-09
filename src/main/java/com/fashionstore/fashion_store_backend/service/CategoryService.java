package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.CategoryCreateDto;
import com.fashionstore.fashion_store_backend.model.Category;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.repository.CategoryRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Phương thức tạo danh mục mới
    public Long createCategory(CategoryCreateDto categoryCreateDto) {
        // Chuyển từ DTO sang Entity
        Category category = new Category();
        category.setName(categoryCreateDto.getName());
        category.setSlug(categoryCreateDto.getSlug());

        // Lưu danh mục vào database
        category = categoryRepository.save(category);

        return category.getId();
    }

    // Phương thức cập nhật danh mục
    public void updateCategory(Long id, CategoryCreateDto categoryCreateDto) {
        // Kiểm tra xem danh mục có tồn tại không
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        // Cập nhật thông tin danh mục
        existingCategory.setName(categoryCreateDto.getName());
        existingCategory.setSlug(categoryCreateDto.getSlug());

        // Lưu lại danh mục đã cập nhật
        categoryRepository.save(existingCategory);
    }

    // Phương thức xóa danh mục
    public void deleteCategory(Long id) {
        // Tìm category theo id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        // Xóa mối quan hệ giữa Category và Product
        for (Product product : category.getProducts()) {
            product.getCategories().remove(category);  // Xóa mối quan hệ trong Product
            productRepository.save(product);  // Lưu lại Product sau khi xóa mối quan hệ
        }

        // Xóa Category
        categoryRepository.delete(category);  // Xóa danh mục khỏi cơ sở dữ liệu
    }
}
