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
                })
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Công khai
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.PUBLIC_API_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.PUBLIC_API_POST).permitAll()

                        // User
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.USER_API_GET)
                        .hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.USER_API_POST)
                        .hasAnyAuthority("USER", "ADMIN")

                        // Admin
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.ADMIN_API_GET)
                        .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.ADMIN_API_POST)
                        .hasAuthority("ADMIN")

                        // Staff
                        .requestMatchers(HttpMethod.GET, ApiAccessConfig.STAFF_API_GET)
                        .hasAuthority("STAFF")
                        .requestMatchers(HttpMethod.POST, ApiAccessConfig.STAFF_API_POST)
                        .hasAuthority("STAFF")

                        // Các yêu cầu khác yêu cầu xác thực
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
