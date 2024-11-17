package com.fashionstore.fashion_store_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WebConfig {

    @Autowired
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
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
