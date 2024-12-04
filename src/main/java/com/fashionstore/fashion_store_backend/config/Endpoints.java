package com.fashionstore.fashion_store_backend.config;

public class Endpoints {

    public static final String front_end_host = "http://localhost:3000";
    public static final String front_end_host_admin = "http://localhost:5173";

    // Các endpoint công khai
    public static final String[] PUBLIC_GET_ENDPOINS = {
            "/cartProduct",
            "/cartProduct/**",
            "/category",
            "/category/**",
            "/color",
            "/color/**",
            "/favoriteProduct",
            "/favoriteProduct/**",
            "/feature",
            "/feature/**",
            "/feedback",
            "/feedback/**",
            "/image",
            "/image/**",
            "/paymentMethod",
            "/paymentMethod/**",
            "/product",
            "/product/**",
            "/role",
            "/role/**",
            "/shippingMethod",
            "/shippingMethod/**",
            "/size",
            "/size/**",
            "/user/check-email", // Endpoint kiểm tra email
            "/user/request-password-reset", // Endpoint yêu cầu đặt lại mật khẩu
            "/user/reset-password/**", // Endpoint đặt lại mật khẩu
            "/product-variant/quantity/**",
            "/orders/create", // Endpoint tạo đơn hàng
    };

    public static final String[] PUBLIC_POST_ENDPOINS = {
            "/user/register", // Đăng ký người dùng
            "/api/auth/login" // Đăng nhập
    };

    // Các endpoint dành cho User
    public static final String[] USER_GET_ENDPOINS = {
            "/user/info", // Thông tin người dùng (chỉ cho phép người dùng tự xem)
            "/address", // Danh sách địa chỉ của user
            "user/avatar-and-fullname", // Lấy avatar của người dùng
            "/user/favorite", // Danh sách sản phẩm yêu thích
            "/cart/**",
            "/orders/list", // Lấy danh sách đơn hàng của người dùng
            "/orders/{orderId}", // Lấy thông tin chi tiết đơn hàng của người dùng (chỉ cho phép người dùng xem đơn hàng của mình)
    };

    public static final String[] USER_POST_ENDPOINS = {
            "/feedback", // Người dùng có thể gửi phản hồi
            "/user/update", // Cập nhật thông tin người dùng
            "/user/change-password", // Đổi mật khẩu
            "/address",  // Thêm mới hoặc cập nhật địa chỉ
            "/user/update-avatar",
            "user/favorite/add/**",
            "user/favorite/remove/**",
            "/cart/**",
            "/orders/create", // Người dùng có thể tạo đơn hàng
    };

    // Các endpoint dành cho Admin
    public static final String[] ADMIN_GET_ENDPOINS = {
            "/user",
            "/user/**", // Quản lý người dùng
            "/category",
            "/category/**", // Quản lý danh mục
            "/color",
            "/color/**", // Quản lý màu sắc
            "/feature",
            "/feature/**", // Quản lý đặc điểm
            "/product",
            "/product/**", // Quản lý sản phẩm
            "/address",
            "/cart/**",
            "/orders/all", // Admin có thể xem tất cả đơn hàng (phân trang)
            "/orders/{orderId}", // Admin có thể xem chi tiết đơn hàng của bất kỳ ai
    };

    public static final String[] ADMIN_POST_ENDPOINS = {
            "/address",
            "/cartProduct",
            "/category",
            "/color",
            "/feature",
            "/feedback",
            "/image",
            "/paymentMethod",
            "/product",
            "/role",
            "/shippingMethod",
            "/size",
            "/user/**", // Quản lý thông tin người dùng
            "/user/favourite/add",
            "/cart/**",
            "/orders/create", // Admin cũng có thể tạo đơn hàng nếu cần
    };

    // Các endpoint dành cho Staff
    public static final String[] STAFF_GET_ENDPOINS = {
            "/feedback", // Xem phản hồi
    };

    public static final String[] STAFF_POST_ENDPOINS = {
            "/feedback", // Gửi phản hồi
            "/user/**" // Quản lý thông tin người dùng
    };
}
