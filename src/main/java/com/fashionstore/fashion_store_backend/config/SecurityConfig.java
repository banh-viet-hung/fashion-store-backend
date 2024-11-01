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
                        corsConfig.addAllowedOrigin(Endpoints.front_end_host);
                        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                        corsConfig.addAllowedHeader("*");
                        return corsConfig;
                    });
                })
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Các endpoint công khai
                        .requestMatchers(Endpoints.PUBLIC_GET_ENDPOINS).permitAll()
                        .requestMatchers(Endpoints.PUBLIC_POST_ENDPOINS).permitAll()

                        // Endpoint dành cho USER
                        .requestMatchers(HttpMethod.GET, Endpoints.USER_GET_ENDPOINS).hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.USER_POST_ENDPOINS).hasAnyAuthority("USER", "ADMIN") // Chỉ cho phép USER và ADMIN

                        // Endpoint dành cho STAFF (không có quyền POST để cập nhật user)
                        .requestMatchers(HttpMethod.GET, Endpoints.STAFF_GET_ENDPOINS).hasAnyAuthority("STAFF")
                        .requestMatchers(HttpMethod.POST, Endpoints.STAFF_POST_ENDPOINS).hasAnyAuthority("STAFF")

                        // Endpoint dành cho ADMIN
                        .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_ENDPOINS).hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_ENDPOINS).hasAnyAuthority("ADMIN")

                        // Tất cả các yêu cầu khác yêu cầu xác thực
                        .anyRequest().authenticated()
                )
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
