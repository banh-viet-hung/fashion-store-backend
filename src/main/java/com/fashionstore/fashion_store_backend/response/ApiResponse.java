package com.fashionstore.fashion_store_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message; // Thông điệp phản hồi
    private boolean success; // Kết quả thành công hay thất bại
    private Object data; // Dữ liệu trả về, có thể là bất kỳ loại nào

    // Constructor cho trường hợp không có dữ liệu
    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.data = null; // Không có dữ liệu
    }
}
