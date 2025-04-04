package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.CategoryCreateDto;
import com.fashionstore.fashion_store_backend.model.Category;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.repository.CategoryRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public Long createCategory(CategoryCreateDto categoryCreateDto) {
        // Kiểm tra xem slug đã tồn tại hay chưa
        Optional<Category> existingCategory = categoryRepository.findBySlug(categoryCreateDto.getSlug());
        if (existingCategory.isPresent()) {
            throw new RuntimeException("Slug '" + categoryCreateDto.getSlug() + "' đã tồn tại");
        }

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

    @Transactional
    public void deleteManyCategories(List<Long> ids) {
        for (Long id : ids) {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với ID: " + id));

            // Xóa mối quan hệ giữa Category và Product
            for (Product product : category.getProducts()) {
                product.getCategories().remove(category);
                productRepository.save(product);
            }

            // Xóa Category
            categoryRepository.delete(category);
        }
    }

    public List<Category> getChildCategoriesBySlug(String slug) {
        // Tìm danh mục dựa trên slug
        Optional<Category> parentCategoryOpt = categoryRepository.findBySlug(slug);
        if (parentCategoryOpt.isEmpty()) {
            throw new RuntimeException("Danh mục không tồn tại với slug: " + slug);
        }

        Category parentCategory = parentCategoryOpt.get();
        return parentCategory.getChildCategories();
    }

}
