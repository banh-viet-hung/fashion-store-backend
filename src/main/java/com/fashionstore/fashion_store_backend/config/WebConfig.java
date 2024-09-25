package com.fashionstore.fashion_store_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class WebConfig implements RepositoryRestConfigurer {

    private static final String FRONT_END_URL = "http://localhost:3000"; // Địa chỉ front-end

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Cấu hình CORS
        cors.addMapping("/**") // Cho phép tất cả các đường dẫn
                .allowedOrigins(FRONT_END_URL) // Chỉ định origin cụ thể
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Các phương thức cho phép
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true); // Cho phép gửi cookie
    }
}
