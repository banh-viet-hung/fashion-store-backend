package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.UserLoginDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.AuthService; // Thay đổi import
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService; // Sử dụng AuthService

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        String token = authService.login(userLoginDto.getEmail(), userLoginDto.getPassword(), userLoginDto.isRememberMe());
        ApiResponse response = new ApiResponse("Đăng nhập thành công", true, token);
        return ResponseEntity.ok(response);
    }
}
