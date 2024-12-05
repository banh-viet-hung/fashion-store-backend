package com.fashionstore.fashion_store_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {

    // Tên sản phẩm không được bỏ trống
    @NotBlank(message = "Tên sản phẩm không được bỏ trống")
    private String name;

    // Mô tả sản phẩm không được bỏ trống
    @NotBlank(message = "Mô tả sản phẩm không được bỏ trống")
    private String description;

    // Giá gốc phải lớn hơn 0 và không được bỏ trống
    @Min(value = 1, message = "Giá gốc phải lớn hơn 0")
    private double price;

    // Giá khuyến mãi phải lớn hơn 0 nếu có, nếu không có thì mặc định là 0
    @Min(value = 0, message = "Giá khuyến mãi phải không nhỏ hơn 0")
    private double salePrice;

    // Danh sách các slug category không được bỏ trống, có thể có từ 1 hoặc nhiều phần tử
    @NotEmpty(message = "Danh sách danh mục không được bỏ trống")
    private List<String> categorySlugs;

    // Danh sách màu sắc có thể rỗng, một hoặc nhiều phần tử
    private List<String> colorNames;

    // Danh sách kích thước có thể rỗng, một hoặc nhiều phần tử
    private List<String> sizeNames;
}
