package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.OrderService;
import com.fashionstore.fashion_store_backend.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private VnpayService vnpayService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/create-payment-url/{orderId}")
    public ResponseEntity<ApiResponse> createPaymentUrl(@PathVariable("orderId") Long orderId,  // Nhận orderId có kiểu Long từ đường dẫn
                                                        @RequestParam(value = "bankCode", required = false) String bankCode, // Thêm tham số bankCode
                                                        HttpServletRequest request) {
        try {
            // Lấy địa chỉ IP từ request
            String ipAddr = request.getRemoteAddr();

            // Gọi phương thức từ VnpayService để xử lý và lấy thông tin từ orderId
            String paymentUrl = vnpayService.createPaymentUrl(orderId, ipAddr, bankCode);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Tạo URL thanh toán thành công", true, paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Có lỗi xảy ra", false));
        }
    }

    @PostMapping("/update-status/{orderId}")
    public ResponseEntity<ApiResponse> updateOrderStatus(@PathVariable Long orderId) {
        try {
            // Lấy  người dùng từ context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : null;
            orderService.updateOrderStatusToPaidAndPending(orderId, username);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Xác nhận thanh toán thành công", true, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), false));
        }
    }
}
