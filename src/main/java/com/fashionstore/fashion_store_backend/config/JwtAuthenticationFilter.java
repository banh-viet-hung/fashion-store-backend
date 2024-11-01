package com.fashionstore.fashion_store_backend.config;

import com.fashionstore.fashion_store_backend.exception.TokenInvalidException;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.UserDetailsServiceImpl;
import com.fashionstore.fashion_store_backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            if (!processToken(jwt, response)) {
                // Nếu processToken trả về false, không cần tiếp tục
                return;
            }
        }

        // Nếu không có token hoặc token hợp lệ, tiếp tục
        filterChain.doFilter(request, response);
    }

    private boolean processToken(String jwt, HttpServletResponse response) throws IOException {
        try {
            String username = jwtUtil.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && jwtUtil.validateToken(jwt, username)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println(authenticationToken);
                    return true; // Token hợp lệ, trả về true
                }
            }
            return false; // Token không hợp lệ
        } catch (TokenInvalidException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ApiResponse(ex.getMessage(), false)));
            return false; // Đã xử lý lỗi, trả về false
        }
    }


}
