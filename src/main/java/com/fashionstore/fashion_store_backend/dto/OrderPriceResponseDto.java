package com.fashionstore.fashion_store_backend.dto;

import lombok.Data;

@Data
public class OrderPriceResponseDto {
    private double subTotal;
    private double shipping;
    private double discount;
    private double total;
}
