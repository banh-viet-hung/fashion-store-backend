package com.fashionstore.fashion_store_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetailResponseDto {
    private List<OrderItemResponseDto> items;
    private OrderPriceResponseDto priceDetails;
    private AddressResponseDto shippingAddress;
    private List<OrderStatusResponseDto> orderStatusDetails;
}
