package com.fashionstore.fashion_store_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDto {

    private AddressRequestDto address;
    private List<CartProductDTO> cart;
    private ShippingDto shipping;
    private String payment;
}
