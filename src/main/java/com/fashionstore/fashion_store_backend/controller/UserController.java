package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.UserRegistrationDto;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        // Kiểm tra xem mật khẩu và xác nhận mật khẩu có giống nhau không
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse("Mật khẩu và xác nhận mật khẩu không khớp", false));
        }

        User newUser = userService.registerUser(registrationDto);
        ApiResponse response = new ApiResponse("Đăng ký thành công", true, newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Trả về 201 Created
    }
}
