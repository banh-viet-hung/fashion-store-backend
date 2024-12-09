package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.exception.InvalidLoginException;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new InvalidLoginException("Email không tồn tại. Vui lòng kiểm tra lại!");
        }

        // Kiểm tra tài khoản có đang hoạt động không
        if (!user.isActive()) {
            throw new LockedException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên để biết thêm chi tiết!");
        }

        // Lấy vai trò từ User và tạo danh sách GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authority)
                .build();
    }

}
