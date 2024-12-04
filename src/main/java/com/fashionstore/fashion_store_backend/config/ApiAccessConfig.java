package com.fashionstore.fashion_store_backend.config;

public class ApiAccessConfig {

    // Các API công khai (không cần xác thực)
    public static final String[] PUBLIC_API_GET = {
            "/cartProduct", "/cartProduct/**", "/category", "/category/**", "/color", "/color/**", "/favoriteProduct",
            "/favoriteProduct/**", "/feature", "/feature/**", "/feedback", "/feedback/**", "/image", "/image/**",
            "/paymentMethod", "/paymentMethod/**", "/product", "/product/**", "/role", "/role/**", "/shippingMethod",
            "/shippingMethod/**", "/size", "/size/**", "/user/check-email", "/user/request-password-reset",
            "/user/reset-password/**", "/product-variant/quantity/**", "/orders/create"
    };

    public static final String[] PUBLIC_API_POST = {
            "/user/register", "/api/auth/login"
    };

    // Các API yêu cầu vai trò User hoặc Admin
    public static final String[] USER_API_GET = {
            "/user/info", "/address", "/user/avatar-and-fullname", "/user/favorite", "/cart/**", "/orders/list", "/orders/{orderId}"
    };

    public static final String[] USER_API_POST = {
            "/feedback", "/user/update", "/user/change-password", "/address", "/user/update-avatar", "/user/favorite/add/**",
            "/user/favorite/remove/**", "/cart/**", "/orders/create"
    };

    // Các API yêu cầu vai trò Admin (Thêm API orders/all vào đây)
    public static final String[] ADMIN_API_GET = {
            "/user", "/user/**", "/category", "/category/**", "/color", "/color/**", "/feature", "/feature/**", "/product",
            "/product/**", "/address", "/cart/**", "/orders/all", "/orders/{orderId}" // <-- Thêm vào đây
    };

    public static final String[] ADMIN_API_POST = {
            "/address", "/cartProduct", "/category", "/color", "/feature", "/feedback", "/image", "/paymentMethod", "/product",
            "/role", "/shippingMethod", "/size", "/user/**", "/user/favourite/add", "/cart/**", "/orders/create"
    };

    // Các API yêu cầu vai trò Staff
    public static final String[] STAFF_API_GET = {
            "/feedback"
    };

    public static final String[] STAFF_API_POST = {
            "/feedback", "/user/**"
    };
}
