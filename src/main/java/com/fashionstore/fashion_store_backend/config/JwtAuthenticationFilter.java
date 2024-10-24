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
            processToken(jwt, response);
        }

        filterChain.doFilter(request, response);
    }

    private void processToken(String jwt, HttpServletResponse response) throws IOException {
        try {
            String username = jwtUtil.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("Processing token for " + username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null && jwtUtil.validateToken(jwt, username)) {
                    System.out.println("Token is valid");
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    System.out.println(authenticationToken);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null) {
                        System.out.println("Authentication set successfully: " + authentication);
                    } else {
                        System.out.println("Authentication was not set.");
                    }
                }
            }
        } catch (TokenInvalidException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ApiResponse("Invalid Token", false)));
        }
    }
}
