package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.CartProductDTO;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.CartProductService;
import com.fashionstore.fashion_store_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartProductController {

    @Autowired
    private CartProductService cartProductService;

    @Autowired
    private UserService userService;

    // API thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addToCart(@RequestBody CartProductDTO cartProductDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            cartProductService.addToCart(username, cartProductDTO);
            return ResponseEntity.ok(new ApiResponse("Thêm sản phẩm vào giỏ hàng thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateCart(@RequestBody CartProductDTO cartProductDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            cartProductService.updateCart(username, cartProductDTO);
            return ResponseEntity.ok(new ApiResponse("Cập nhật giỏ hàng thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove/{productId}/{size}/{color}")
    public ResponseEntity<ApiResponse> removeFromCart(@PathVariable Long productId, @PathVariable String size, @PathVariable String color) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token
        if (size.equals("null")) {
            size = null;
        }
        if (color.equals("null")) {
            color = null;
        }

        try {
            cartProductService.removeFromCart(username, productId, size, color);
            return ResponseEntity.ok(new ApiResponse("Xóa sản phẩm khỏi giỏ hàng thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

//     API lấy giỏ hàng của người dùng
    @GetMapping("/")
    public ResponseEntity<ApiResponse> getCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            List<CartProductDTO> cart = cartProductService.getCart(username);
            return ResponseEntity.ok(new ApiResponse("Lấy giỏ hàng thành công", true, cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }
}
