package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String password, boolean rememberMe) {
        // Thực hiện xác thực
        // Đoạn xác thực này sẽ ném ra một ngoại lệ nếu thông tin đăng nhập không hợp lệ
        //  Đoạn mã này giúp bạn xác thực người dùng bằng cách kiểm tra tên đăng nhập và mật khẩu của họ. Nếu xác thực thành công, người dùng sẽ được phép truy cập vào các tài nguyên được bảo vệ của ứng dụng.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authenticationToken);

        // Lấy thông tin người dùng
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        List<String> roles = userDetails.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toList());

        // Tạo và trả về JWT
        return jwtUtil.generateToken(email, roles, rememberMe);
    }
}
