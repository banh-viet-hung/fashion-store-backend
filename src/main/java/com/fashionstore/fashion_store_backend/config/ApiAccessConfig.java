package com.fashionstore.fashion_store_backend.config;

public class ApiAccessConfig {

        // Các API công khai (không cần xác thực)
        public static final String[] PUBLIC_API_GET = { "/cartProduct", "/cartProduct/**", "/category", "/category/**",
                        "/color", "/color/**", "/favoriteProduct", "/favoriteProduct/**", "/feature", "/feature/**",
                        "/feedback",
                        "/feedback/**", "/image", "/image/**", "/paymentMethod", "/paymentMethod/**", "/product",
                        "/product/**",
                        "/role", "/role/**", "/shippingMethod", "/shippingMethod/**", "/size", "/size/**",
                        "/user/check-email",
                        "/user/request-password-reset", "/user/reset-password/**", "/product-variant/quantity/**",
                        "/products/{productId}", "/user/check-status", "/payment/create-payment-url/{orderId}",
                        "/categories/children/{slug}", "/products/list", "/orderStatus",
                        "/feedback//product/{productId}" };

        public static final String[] PUBLIC_API_POST = { "/user/register", "/api/auth/login", "orders/create",
                        "/payment/update-status/{orderId}", "/orders/{orderId}/cancel", "/products/filter",
                        "/user/check-email",
                        "/user/request-password-reset", "/user/reset-password/{token}", "/coupons/validate" };

        // Các API yêu cầu vai trò ADMIN, STAFF, USER (Tất cả các role có thể truy cập)
        public static final String[] ADMIN_STAFF_USER_API_GET = { "/user/info", "/address", "/user/avatar-and-fullname",
                        "/user/favorite", "/cart/**", "/orders/list", "/orders/{orderId}", "/feedback/user" };

        public static final String[] ADMIN_STAFF_USER_API_POST = { "/user/update", "/user/change-password", "/address",
                        "/user/update-avatar", "/user/favorite/add/**", "/user/favorite/remove/**", "/cart/**",
                        "/feedback/update/**" };

        // Các API yêu cầu vai trò STAFF hoặc ADMIN (Chỉ dành cho STAFF và ADMIN, không
        // phải USER)
        public static final String[] STAFF_ADMIN_API_GET = { "/orders/all", "/orders/user/{userId}",
                        "/coupons/list", "/coupons/get/{id}", "/user/all" };

        public static final String[] STAFF_ADMIN_API_POST = { "/orders/{orderId}/update-status", "/products/create",
                        "/product-variant/{productId}/variants", "products/{productId}/images",
                        "/products/delete/{productId}",
                        "/products/update/{productId}", "/categories/create", "/categories/{id}",
                        "/products/delete-many",
                        "/categories/delete-many", "/coupons/create", "/coupons/edit/{id}", "/coupons/delete-many" };

        public static final String[] ADMIN_API_GET = { "/user/info/{username}", "/dashboard/**",
                        "/dashboard/statistics/**",
                        "/dashboard/sales/**", "/dashboard/orders/**", "/dashboard/inventory/**" };

        public static final String[] ADMIN_API_POST = { "/user/update-role/{username}",
                        "/user/change-status/{username}",
                        "/user/admin/create-user" };
}
