package com.fashionstore.fashion_store_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterRequestDto {
    private List<String> categorySlugs;
    private List<String> sizeNames;
    private List<String> colorNames;
    @Min(value = 0, message = "Giá thấp nhất phải lớn hơn hoặc bằng 0")
    private Double minPrice;
    @Min(value = 0, message = "Giá cao nhất phải lớn hơn hoặc bằng 0")
    private Double maxPrice;
    @Min(value = 0, message = "Trang phải lớn hơn hoặc bằng 0")
    private int page;
    @Min(value = 1, message = "Kích thước trang phải lớn hơn hoặc bằng 1")
    private int size;
    private String sortBy;  // Các giá trị có thể: "priceAsc", "priceDesc", "newest"
}
