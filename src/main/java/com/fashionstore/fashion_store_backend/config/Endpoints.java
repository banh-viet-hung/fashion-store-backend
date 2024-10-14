package com.fashionstore.fashion_store_backend.config;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";

    // Các endpoint công khai
    public static final String[] PUBLIC_GET_ENDPOINS = {"/address", "/address/**", "/cartProduct", "/cartProduct/**", "/category", "/category/**", "/color", "/color/**", "/favoriteProduct", "/favoriteProduct/**", "/feature", "/feature/**", "/feedback", "/feedback/**", "/image", "/image/**", "/order", "/order/**", "/paymentMethod", "/paymentMethod/**", "/product", "/product/**", "/role", "/role/**", "/shippingMethod", "/shippingMethod/**", "/size", "/size/**", "/user/check-email", // Endpoint kiểm tra email
            "/user/request-password-reset", // Endpoint yêu cầu đặt lại mật khẩu
            "/user/reset-password/**" // Endpoint đặt lại mật khẩu
    };

    public static final String[] PUBLIC_POST_ENDPOINS = {"/user/register" // Đăng ký người dùng
    };

    // Các endpoint dành cho User
    public static final String[] USER_GET_ENDPOINS = {"/user", // Thông tin người dùng
            "/user/**", "/order" // Danh sách đơn hàng của user
    };

    public static final String[] USER_POST_ENDPOINS = {"/feedback" // Người dùng có thể gửi phản hồi
    };

    // Các endpoint dành cho Admin
    public static final String[] ADMIN_GET_ENDPOINS = {"/user", "/user/**", "/category", "/category/**", "/color", "/color/**", "/feature", "/feature/**", "/product", "/product/**"};

    public static final String[] ADMIN_POST_ENDPOINS = {"/address", "/cartProduct", "/category", "/color", "/feature", "/feedback", "/image", "/order", "/paymentMethod", "/product", "/role", "/shippingMethod", "/size"};

    // Các endpoint dành cho Staff
    public static final String[] STAFF_GET_ENDPOINS = {"/order", "/order/**", "/feedback"};

    public static final String[] STAFF_POST_ENDPOINS = {"/feedback"};
}
