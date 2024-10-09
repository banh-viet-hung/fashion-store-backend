package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.UserRegistrationDto;
import com.fashionstore.fashion_store_backend.exception.EmailAlreadyExistsException;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
//            throw new RuntimeException("Email đã tồn tại");
            throw new EmailAlreadyExistsException("Email đã tồn tại");
        }

        // Tạo mới người dùng
        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        // user.setPassword(passwordEncoder.encode(registrationDto.getPassword())); // Mã hóa mật khẩu
        user.setPassword(registrationDto.getPassword()); // Để mật khẩu chưa mã hóa

        // Lưu người dùng vào cơ sở dữ liệu
        return userRepository.save(user);
    }
}
