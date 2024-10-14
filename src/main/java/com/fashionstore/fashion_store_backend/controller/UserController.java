package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.ResetPasswordDto;
import com.fashionstore.fashion_store_backend.dto.UserRegistrationDto;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

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

}
