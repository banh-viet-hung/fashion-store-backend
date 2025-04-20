package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.FeedbackResponseDto;
import com.fashionstore.fashion_store_backend.dto.FeedbackUpdateDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * API lấy danh sách các feedback của người dùng hiện tại
     * 
     * @return Danh sách chi tiết các đánh giá của người dùng hiện tại
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getUserFeedbacks() {
        // Lấy email người dùng từ xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getUserFeedbacksDto(username);
            return ResponseEntity.ok(new ApiResponse("Danh sách đánh giá của người dùng", true, feedbacks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
    
    /**
     * API cập nhật feedback dựa vào ID
     * 
     * @param feedbackId ID của feedback cần cập nhật
     * @param updateDto Thông tin cập nhật (comment và rating)
     * @return Feedback đã được cập nhật
     */
    @PutMapping("/update/{feedbackId}")
    public ResponseEntity<ApiResponse> updateFeedback(
            @PathVariable Long feedbackId,
            @Valid @RequestBody FeedbackUpdateDto updateDto) {
        
        // Lấy email người dùng từ xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        try {
            FeedbackResponseDto updatedFeedback = feedbackService.updateFeedback(feedbackId, username, updateDto);
            return ResponseEntity.ok(new ApiResponse("Cập nhật đánh giá thành công", true, updatedFeedback));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
    
    /**
     * API công khai lấy danh sách các đánh giá của một sản phẩm
     * 
     * @param productId ID của sản phẩm cần lấy đánh giá
     * @return Danh sách các đánh giá công khai của sản phẩm
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> getProductFeedbacks(@PathVariable Long productId) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getProductFeedbacks(productId);
            return ResponseEntity.ok(new ApiResponse("Danh sách đánh giá của sản phẩm", true, feedbacks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }
} 