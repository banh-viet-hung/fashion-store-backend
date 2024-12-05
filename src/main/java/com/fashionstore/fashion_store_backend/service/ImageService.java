package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.ProductImagesCreateDto;
import com.fashionstore.fashion_store_backend.model.Image;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.repository.ImageRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    // Hàm tạo ảnh cho sản phẩm
    public void createProductImages(Long productId, ProductImagesCreateDto requestDto) throws Exception {
        // Lấy sản phẩm từ ID
        Product product = productRepository.findById(productId).orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));

        // Kiểm tra danh sách URL ảnh có hợp lệ không (tối thiểu 1, tối đa 6 ảnh)
        List<String> imageUrls = requestDto.getImageUrls();
        if (imageUrls.size() < 1 || imageUrls.size() > 6) {
            throw new Exception("Danh sách ảnh phải có ít nhất 1 ảnh và tối đa 6 ảnh");
        }

        // Xử lý ảnh thumbnail
        List<Image> images = imageUrls.stream()
                .map(url -> {
                    Image image = new Image();
                    image.setUrl(url);
                    image.setProduct(product);
                    image.setThumbnail(false);  // Mặc định ảnh không phải thumbnail
                    return image;
                }).collect(Collectors.toList());

        // Quy định số lượng ảnh thumbnail cần có
        int thumbnailCount = 0;

        if (imageUrls.size() == 1) {
            thumbnailCount = 1;  // Nếu có 1 ảnh, ảnh đó phải là thumbnail
        } else if (imageUrls.size() == 2) {
            thumbnailCount = 2;  // Nếu có 2 ảnh, cả 2 ảnh đều là thumbnail
        } else if (imageUrls.size() >= 3) {
            thumbnailCount = 2;  // Nếu có 3 ảnh trở lên, 2 ảnh đầu tiên là thumbnail
        }

        // Đánh dấu ảnh thumbnail
        for (int i = 0; i < thumbnailCount; i++) {
            Image image = images.get(i);
            image.setThumbnail(true);  // Đánh dấu ảnh là thumbnail
        }

        // Lưu ảnh vào cơ sở dữ liệu
        imageRepository.saveAll(images);
    }
}
