package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.OrderCreateDto;
import com.fashionstore.fashion_store_backend.dto.OrderDetailResponseDto;
import com.fashionstore.fashion_store_backend.dto.OrderResponseDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderCreateDto orderCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        try {
            Long newOrderId = orderService.createOrder(orderCreateDto, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Đơn hàng đã được tạo thành công", true, newOrderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    // API lấy danh sách đơn hàng của người dùng
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName(); // Lấy username từ token
        }

        try {
            List<OrderResponseDto> orders = orderService.getOrdersByUsername(username); // Gọi service để lấy danh sách đơn hàng
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Danh sách đơn hàng của người dùng", true, orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Lỗi server", false));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            OrderDetailResponseDto orderResponseDto = orderService.getOrderById(orderId, username);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Thông tin đơn hàng", true, orderResponseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    // Thêm API phân trang vào OrderController
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllOrdersWithPagination(@RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        try {

            // Gọi service để lấy danh sách đơn hàng phân trang
            Page<OrderResponseDto> orderPage = orderService.getAllOrdersWithPagination(page, size);

            // Tạo phản hồi trả về
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Danh sách đơn hàng", true, orderPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Lỗi server", false));
        }
    }

    // API để tạo trạng thái chi tiết đơn hàng mới
    @PostMapping("/{orderId}/update-status")
    public ResponseEntity<ApiResponse> updateOrderStatus(@PathVariable Long orderId,
                                                         @RequestParam String statusCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        try {
            orderService.updateOrderStatus(orderId, statusCode, username);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Trạng thái chi tiết đơn hàng đã được cập nhật thành công", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

}
