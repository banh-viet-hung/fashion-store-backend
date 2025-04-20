package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.FeedbackResponseDto;
import com.fashionstore.fashion_store_backend.dto.FeedbackUpdateDto;
import com.fashionstore.fashion_store_backend.model.Feedback;
import com.fashionstore.fashion_store_backend.model.Order;
import com.fashionstore.fashion_store_backend.model.OrderDetail;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.FeedbackRepository;
import com.fashionstore.fashion_store_backend.repository.ProductRepository;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;

    /**
     * Tạo các feedback tự động khi một đơn hàng chuyển sang trạng thái "DELIVERED"
     * 
     * @param order Đơn hàng đã được giao
     * @return Danh sách các feedback đã được tạo
     */
    @Transactional
    public List<Feedback> createFeedbacksForDeliveredOrder(Order order) {
        List<Feedback> createdFeedbacks = new ArrayList<>();
        
        // Chỉ tạo feedback cho đơn hàng của người dùng đã đăng nhập (không phải đơn hàng vãng lai)
        User user = order.getUser();
        if (user == null) {
            return createdFeedbacks; // Trả về danh sách rỗng nếu là đơn hàng vãng lai
        }
        
        // Tạo feedback cho từng sản phẩm trong đơn hàng
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            // Tạo feedback mới
            Feedback feedback = new Feedback();
            feedback.setProduct(orderDetail.getProduct());
            feedback.setUser(user);
            feedback.setColor(orderDetail.getColor() != null ? orderDetail.getColor() : "Default");
            feedback.setSize(orderDetail.getSize() != null ? orderDetail.getSize() : "Default");
            feedback.setCreatedAt(null); // Thời gian tạo sẽ được set khi người dùng đánh giá
            feedback.setUpdatedAt(null); // Thời gian cập nhật sẽ được set khi người dùng đánh giá
            feedback.setPublic(false); // Chưa công khai cho đến khi người dùng đánh giá
            
            // Lưu feedback
            Feedback savedFeedback = feedbackRepository.save(feedback);
            createdFeedbacks.add(savedFeedback);
        }
        
        return createdFeedbacks;
    }
    
    /**
     * Cập nhật đánh giá sản phẩm
     * 
     * @param feedbackId ID của feedback cần cập nhật
     * @param username Email của người dùng (được lấy từ authentication)
     * @param updateDto DTO chứa thông tin cập nhật (comment và rating)
     * @return Feedback đã được cập nhật
     */
    @Transactional
    public FeedbackResponseDto updateFeedback(Long feedbackId, String username, FeedbackUpdateDto updateDto) {
        // Tìm feedback theo ID
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + feedbackId));
        
        // Kiểm tra xem người dùng có quyền cập nhật feedback này không
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        
        if (!feedback.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật đánh giá này");
        }
        
        // Kiểm tra trạng thái feedback và cập nhật tương ứng
        if (!feedback.isPublic()) {
            // Feedback chưa được công khai, cho phép cập nhật và đánh dấu là public
            feedback.setPublic(true);
            feedback.setComment(updateDto.getComment());
            feedback.setRating(updateDto.getRating());
            feedback.setCreatedAt(LocalDateTime.now());
        } else {
            // Feedback đã được công khai, kiểm tra xem đã chỉnh sửa chưa
            if (!feedback.isEdited()) {
                // Feedback chưa được chỉnh sửa, cho phép cập nhật một lần
                feedback.setEdited(true);
                feedback.setComment(updateDto.getComment());
                feedback.setRating(updateDto.getRating());
                feedback.setUpdatedAt(LocalDateTime.now());
            } else {
                // Feedback đã được chỉnh sửa trước đó, không cho phép cập nhật nữa
                throw new RuntimeException("Feedback này đã được chỉnh sửa trước đó, không thể cập nhật lại");
            }
        }
        
        // Lưu feedback đã cập nhật
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        
        // Cập nhật đánh giá trung bình của sản phẩm
        updateProductAverageRating(feedback.getProduct().getId());
        
        // Chuyển đổi feedback thành DTO và trả về
        return convertToDto(updatedFeedback);
    }
    
    /**
     * Lấy danh sách các đánh giá công khai của một sản phẩm
     * 
     * @param productId ID của sản phẩm cần lấy đánh giá
     * @return Danh sách các đánh giá công khai của sản phẩm
     */
    public List<FeedbackResponseDto> getProductFeedbacks(Long productId) {
        // Kiểm tra xem sản phẩm có tồn tại không
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
        
        // Lấy danh sách các đánh giá công khai của sản phẩm
        List<Feedback> feedbacks = feedbackRepository.findByProductIdAndIsPublicTrue(productId);
        
        // Chuyển đổi các đánh giá thành DTO và trả về
        return feedbacks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật đánh giá trung bình của sản phẩm
     * 
     * @param productId ID của sản phẩm cần cập nhật đánh giá trung bình
     */
    @Transactional
    public void updateProductAverageRating(Long productId) {
        // Lấy danh sách tất cả feedback công khai của sản phẩm
        List<Feedback> publicFeedbacks = feedbackRepository.findByProductIdAndIsPublicTrue(productId);
        
        // Nếu không có feedback công khai nào, đặt đánh giá trung bình là 0
        if (publicFeedbacks.isEmpty()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
            product.setAverageRating(0);
            productRepository.save(product);
            return;
        }
        
        // Tính đánh giá trung bình
        double totalRating = 0;
        for (Feedback feedback : publicFeedbacks) {
            totalRating += feedback.getRating();
        }
        
        double averageRating = totalRating / publicFeedbacks.size();
        // Làm tròn đến 1 chữ số thập phân
        averageRating = Math.round(averageRating * 10) / 10.0;
        
        // Cập nhật đánh giá trung bình cho sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }
    
    /**
     * Lấy danh sách tất cả feedback của người dùng hiện tại
     * 
     * @param username Email của người dùng (được lấy từ authentication)
     * @return Danh sách feedback của người dùng
     */
    public List<Feedback> getUserFeedbacks(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        
        return feedbackRepository.findByUser(user);
    }
    
    /**
     * Lấy danh sách tất cả feedback của người dùng hiện tại và chuyển đổi thành DTO
     * 
     * @param username Email của người dùng (được lấy từ authentication)
     * @return Danh sách DTO chứa thông tin feedback của người dùng
     */
    public List<FeedbackResponseDto> getUserFeedbacksDto(String username) {
        List<Feedback> feedbacks = getUserFeedbacks(username);
        
        return feedbacks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi entity Feedback thành FeedbackResponseDto
     * 
     * @param feedback Feedback cần chuyển đổi
     * @return FeedbackResponseDto
     */
    private FeedbackResponseDto convertToDto(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        
        // Thông tin feedback
        dto.setId(feedback.getId());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setUpdatedAt(feedback.getUpdatedAt());
        dto.setRating(feedback.getRating());
        dto.setPublic(feedback.isPublic());
        dto.setEdited(feedback.isEdited());
        dto.setColor(feedback.getColor());
        dto.setSize(feedback.getSize());
        
        // Thông tin sản phẩm
        if (feedback.getProduct() != null) {
            dto.setProductId(feedback.getProduct().getId());
            dto.setProductName(feedback.getProduct().getName());
            
            // Lấy ảnh đầu tiên của sản phẩm (nếu có)
            if (feedback.getProduct().getImages() != null && !feedback.getProduct().getImages().isEmpty()) {
                dto.setProductImage(feedback.getProduct().getImages().get(0).getUrl());
            }
        }
        
        // Thông tin người dùng
        if (feedback.getUser() != null) {
            dto.setUserEmail(feedback.getUser().getEmail());
            dto.setUserName(feedback.getUser().getFullName());
            dto.setUserAvatar(feedback.getUser().getAvatar());
        }
        
        return dto;
    }
} 