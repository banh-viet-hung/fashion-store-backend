package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.exception.EmailAlreadyExistsException;
import com.fashionstore.fashion_store_backend.model.Role;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.RoleRepository;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email đã tồn tại");
        }

        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Lấy role USER từ database và gán cho người dùng
        Role userRole = roleRepository.findByName("USER");
        if (userRole != null) {
            user.setRole(userRole); // Gán role USER cho người dùng
        }

        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void sendPasswordResetEmail(String email) {
        emailService.sendPasswordResetEmail(email);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);
        userRepository.save(user);
    }

    public UserInfoDto getUserInfo(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        return new UserInfoDto(
                user.getFullName(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getDateOfBirth()
        );
    }

    public void updateUserInfo(String username, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        user.setFullName(userUpdateDto.getFullName());
        user.setPhoneNumber(userUpdateDto.getPhoneNumber());
        user.setGender(userUpdateDto.getGender());
        user.setDateOfBirth(userUpdateDto.getDateOfBirth());

        userRepository.save(user);
    }

    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }


    public UserAvatarDto getUserAvatarAndFullName(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        return new UserAvatarDto(user.getFullName(), user.getAvatar());
    }

}
