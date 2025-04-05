package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.AdminOrderFilterRequestDto;
import com.fashionstore.fashion_store_backend.dto.OrderResponseDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * API dành cho Admin và Staff để lấy danh sách tất cả các đơn hàng với khả năng lọc và phân trang
     * @param page Số trang, mặc định là 1
     * @param size Số lượng đơn hàng trên mỗi trang, mặc định là 10
     * @param orderId Mã đơn hàng để lọc (tùy chọn)
     * @param orderStatusCode Mã trạng thái đơn hàng để lọc (tùy chọn)
     * @param paymentMethodCode Mã phương thức thanh toán để lọc (tùy chọn)
     * @param shippingMethodCode Mã phương thức vận chuyển để lọc (tùy chọn)
     * @param startDate Ngày bắt đầu để lọc, định dạng MM/dd/yyyy (tùy chọn)
     * @param endDate Ngày kết thúc để lọc, định dạng MM/dd/yyyy (tùy chọn)
     * @return Danh sách đơn hàng đã được lọc và phân trang
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderStatusCode,
            @RequestParam(required = false) String paymentMethodCode,
            @RequestParam(required = false) String shippingMethodCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") LocalDate endDate
    ) {
        try {
            // Tạo đối tượng DTO để truyền các tham số lọc
            AdminOrderFilterRequestDto filterDto = new AdminOrderFilterRequestDto(
                    orderId, orderStatusCode, paymentMethodCode, shippingMethodCode, startDate, endDate
            );

            // Gọi service để lấy danh sách đơn hàng theo các điều kiện lọc
            Page<OrderResponseDto> orderPage = orderService.getOrdersForAdmin(page, size, filterDto);

            // Trả về kết quả
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Danh sách đơn hàng", true, orderPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }
} 