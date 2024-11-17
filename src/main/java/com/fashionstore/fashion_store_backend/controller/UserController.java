package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.FavoriteProductService;
import com.fashionstore.fashion_store_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteProductService favoriteProductService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Mật khẩu và xác nhận mật khẩu không khớp", false));
        }

        User newUser = userService.registerUser(registrationDto);
        ApiResponse response = new ApiResponse("Đăng ký thành công", true, newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email không được để trống", false));
        }

        boolean exists = userService.emailExists(email);
        if (exists) {
            return ResponseEntity.ok(new ApiResponse("Email tồn tại", true));
        } else {
            return ResponseEntity.ok(new ApiResponse("Không tồn tại email", false));
        }
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Email không được để trống", false));
        }

        userService.sendPasswordResetEmail(email);
        return ResponseEntity.ok(new ApiResponse("Email đặt lại mật khẩu đã được gửi", true));
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<ApiResponse> resetPassword(@PathVariable String token, @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Mật khẩu và xác nhận mật khẩu không khớp", false));
        }

        try {
            userService.resetPassword(token, resetPasswordDto.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Mật khẩu đã được đặt lại thành công", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        UserInfoDto userInfo = userService.getUserInfo(username);
        return ResponseEntity.ok(new ApiResponse("Thông tin người dùng", true, userInfo));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        userService.updateUserInfo(username, userUpdateDto);
        return ResponseEntity.ok(new ApiResponse("Cập nhật thông tin thành công", true));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            userService.changePassword(username, changePasswordDto);
            return ResponseEntity.ok(new ApiResponse("Mật khẩu đã được đổi thành công", true));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/avatar-and-fullname")
    public ResponseEntity<ApiResponse> getUserAvatarAndFullName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        UserAvatarDto userAvatarDto = userService.getUserAvatarAndFullName(username);
        return ResponseEntity.ok(new ApiResponse("Thông tin người dùng", true, userAvatarDto));
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Lấy email từ token

        String avatarUrl = request.get("avatar");

        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("URL avatar không được để trống", false));
        }

        userService.updateUserAvatar(email, avatarUrl);
        return ResponseEntity.ok(new ApiResponse("Cập nhật avatar thành công", true));
    }

    // API thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/favorite/add/{productId}")
    public ResponseEntity<ApiResponse> addToFavorites(@PathVariable Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            favoriteProductService.addProductToFavorites(username, productId);
            return ResponseEntity.ok(new ApiResponse("Thêm sản phẩm vào danh sách yêu thích thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/favorite/remove/{productId}")
    public ResponseEntity<ApiResponse> removeFromFavorites(@PathVariable Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            favoriteProductService.removeProductFromFavorites(username, productId);
            return ResponseEntity.ok(new ApiResponse("Xóa sản phẩm khỏi danh sách yêu thích thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API lấy danh sách các sản phẩm yêu thích
    @GetMapping("/favorite")
    public ResponseEntity<ApiResponse> getFavoriteProducts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        try {
            // Lấy danh sách các sản phẩm yêu thích theo ID
            List<Long> favoriteProductIds = favoriteProductService.getFavoriteProductIds(username);
            return ResponseEntity.ok(new ApiResponse("Danh sách sản phẩm yêu thích", true, favoriteProductIds));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }
}
