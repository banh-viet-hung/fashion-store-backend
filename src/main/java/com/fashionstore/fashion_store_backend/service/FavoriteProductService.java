package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.model.FavoriteProduct;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.FavoriteProductRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteProductService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FavoriteProductRepository favoriteProductRepository;

    // Thêm sản phẩm vào danh sách yêu thích của người dùng
    @Transactional
    public void addProductToFavorites(String username, Long productId) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích của người dùng chưa
        for (FavoriteProduct favoriteProduct : user.getFavoriteProducts()) {
            if (favoriteProduct.getProduct().getId().equals(productId)) {
                throw new RuntimeException("Sản phẩm đã có trong danh sách yêu thích");
            }
        }

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setUser(user);
        favoriteProduct.setProduct(product);

        favoriteProductRepository.save(favoriteProduct);
    }

    @Transactional
    public void removeProductFromFavorites(String username, Long productId) {
        // Lấy người dùng từ repository
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        // Tìm FavoriteProduct từ repository để đảm bảo thực thể đã được quản lý
        FavoriteProduct favoriteProduct = favoriteProductRepository.findByUserAndProductId(user, productId).orElseThrow(() -> new RuntimeException("Sản phẩm không có trong danh sách yêu thích"));

        favoriteProductRepository.delete(favoriteProduct);
    }

    // Lấy danh sách các sản phẩm yêu thích của người dùng
    @Transactional(readOnly = true)
    public List<Long> getFavoriteProductIds(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        // Trả về danh sách các ID sản phẩm yêu thích
        return user.getFavoriteProducts().stream()
                .map(favoriteProduct -> favoriteProduct.getProduct().getId())
                .collect(Collectors.toList());
    }

}
