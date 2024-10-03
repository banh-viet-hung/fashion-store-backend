package com.fashionstore.fashion_store_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.Size;
import com.fashionstore.fashion_store_backend.model.Color;
import com.fashionstore.fashion_store_backend.model.Image;
import com.fashionstore.fashion_store_backend.model.Category;
import com.fashionstore.fashion_store_backend.model.Feedback;
import com.fashionstore.fashion_store_backend.model.OrderDetail;
import com.fashionstore.fashion_store_backend.model.FavoriteProduct;
import com.fashionstore.fashion_store_backend.model.CartProduct;
import com.fashionstore.fashion_store_backend.model.Feature;

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

        // Expose ID cho tất cả các entity
        config.exposeIdsFor(
                Product.class,
                Size.class,
                Color.class,
                Image.class,
                Category.class,
                Feedback.class,
                OrderDetail.class,
                FavoriteProduct.class,
                CartProduct.class,
                Feature.class
        );
    }
}