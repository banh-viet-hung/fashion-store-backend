package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.CartProductDTO;
import com.fashionstore.fashion_store_backend.model.CartProduct;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.CartProductRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartProductService {

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(String username, CartProductDTO cartProductDTO) throws Exception {
        // Lấy người dùng từ repository
        User user = userRepository.findByEmail(username);

        // Lấy sản phẩm từ repository
        Product product = productRepository.findById(cartProductDTO.getProductId())
                .orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));

        // Tạo đối tượng CartProduct
        CartProduct cartProduct = new CartProduct();
        cartProduct.setUser(user);
        cartProduct.setProduct(product);
        cartProduct.setSizeName(cartProductDTO.getSize());
        cartProduct.setColorName(cartProductDTO.getColor());
        cartProduct.setQuantity(cartProductDTO.getQuantity());

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa, xử lý trường hợp size và color là null
        CartProduct existingCartProduct = cartProductRepository.findByUserAndProductAndSizeNameAndColorName(user, product,
                        cartProduct.getSizeName(), cartProduct.getColorName())
                .orElse(null);

        if (existingCartProduct != null) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            existingCartProduct.setQuantity(existingCartProduct.getQuantity() + cartProductDTO.getQuantity());
            cartProductRepository.save(existingCartProduct);
        } else {
            // Nếu chưa có, thêm sản phẩm mới vào giỏ hàng
            cartProductRepository.save(cartProduct);
        }
    }



    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public void updateCart(String username, CartProductDTO cartProductDTO) throws Exception {
        User user = userRepository.findByEmail(username);

        // Tìm sản phẩm trong giỏ hàng của người dùng với điều kiện size và color có thể là null
        CartProduct existingCartProduct = cartProductRepository
                .findAll()
                .stream()
                .filter(cp -> cp.getUser().getId().equals(user.getId()) &&
                        cp.getProduct().getId().equals(cartProductDTO.getProductId()) &&
                        (cp.getSizeName() == null ? cartProductDTO.getSize() == null : cp.getSizeName().equals(cartProductDTO.getSize())) &&
                        (cp.getColorName() == null ? cartProductDTO.getColor() == null : cp.getColorName().equals(cartProductDTO.getColor())))
                .findFirst()
                .orElseThrow(() -> new Exception("Sản phẩm không tồn tại trong giỏ hàng"));

        // Cập nhật số lượng sản phẩm
        existingCartProduct.setQuantity(cartProductDTO.getQuantity());
        cartProductRepository.save(existingCartProduct);
    }


    // Xóa sản phẩm khỏi giỏ hàng
    public void removeFromCart(String username, Long productId, String size, String color) throws Exception {
        User user = userRepository.findByEmail(username);

        // Tìm sản phẩm trong giỏ hàng với size và color có thể là null
        CartProduct cartProduct = cartProductRepository
                .findAll()
                .stream()
                .filter(cp -> cp.getUser().getId().equals(user.getId()) &&
                        cp.getProduct().getId().equals(productId) &&
                        (cp.getSizeName() == null ? size == null : cp.getSizeName().equals(size)) &&
                        (cp.getColorName() == null ? color == null : cp.getColorName().equals(color)))
                .findFirst()
                .orElseThrow(() -> new Exception("Sản phẩm không tồn tại trong giỏ hàng"));

        // Xóa sản phẩm khỏi giỏ hàng
        cartProductRepository.delete(cartProduct);
    }


    // Lấy giỏ hàng của người dùng
    public List<CartProductDTO> getCart(String username) throws Exception {
        User user = userRepository.findByEmail(username);
        List<CartProduct> cartProducts = cartProductRepository.findByUser(user);

        return cartProducts.stream().map(cartProduct -> {
            CartProductDTO cartProductDTO = new CartProductDTO();
            cartProductDTO.setProductId(cartProduct.getProduct().getId());
            cartProductDTO.setSize(cartProduct.getSizeName());
            cartProductDTO.setColor(cartProduct.getColorName());
            cartProductDTO.setQuantity(cartProduct.getQuantity());
            return cartProductDTO;
        }).collect(Collectors.toList());
    }
}
