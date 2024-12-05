package com.fashionstore.fashion_store_backend.config;

import com.fashionstore.fashion_store_backend.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration corsConfig = new CorsConfiguration();
                        corsConfig.addAllowedOrigin("http://localhost:3000");
                        corsConfig.addAllowedOrigin("http://localhost:5173");
                        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                        corsConfig.addAllowedHeader("*");
                        return corsConfig;
                    });
                }).csrf(csrf -> csrf.disable()).authorizeHttpRequests(authz -> authz

                        // 1. Nhóm ADMIN (chỉ dành riêng cho ADMIN)
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.ADMIN_API_GET).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.ADMIN_API_POST).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiAccessConfig.ADMIN_API_POST).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiAccessConfig.ADMIN_API_POST).hasAuthority("ADMIN")

                        // 2. Nhóm STAFF, ADMIN (yêu cầu Staff hoặc Admin)
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.STAFF_ADMIN_API_GET).hasAnyAuthority("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.STAFF_ADMIN_API_POST).hasAnyAuthority("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiAccessConfig.STAFF_ADMIN_API_POST).hasAnyAuthority("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiAccessConfig.STAFF_ADMIN_API_POST).hasAnyAuthority("STAFF", "ADMIN")

                        // 3. Nhóm ADMIN, STAFF, USER (yêu cầu ít nhất một trong các quyền ADMIN, STAFF, USER)
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.ADMIN_STAFF_USER_API_GET).hasAnyAuthority("USER", "STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.ADMIN_STAFF_USER_API_POST).hasAnyAuthority("USER", "STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiAccessConfig.ADMIN_STAFF_USER_API_POST).hasAnyAuthority("USER", "STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiAccessConfig.ADMIN_STAFF_USER_API_POST).hasAnyAuthority("USER", "STAFF", "ADMIN")

                        // 4. Nhóm public (không yêu cầu xác thực)
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.PUBLIC_API_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.PUBLIC_API_POST).permitAll()
                        .requestMatchers(HttpMethod.PUT, ApiAccessConfig.PUBLIC_API_POST).permitAll()
                        .requestMatchers(HttpMethod.DELETE, ApiAccessConfig.PUBLIC_API_POST).permitAll()

                        // Các yêu cầu khác yêu cầu xác thực
                        .anyRequest().authenticated())

                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }
}
