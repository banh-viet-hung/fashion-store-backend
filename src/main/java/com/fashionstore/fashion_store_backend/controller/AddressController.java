package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.AddressRequestDto;
import com.fashionstore.fashion_store_backend.dto.AddressResponseDto;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin(origins = "http://localhost:3000")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getAddressesForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        List<AddressResponseDto> addresses = addressService.getUserAddresses(username);
        if (addresses.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse("Không tìm thấy địa chỉ nào", false));
        }

        return ResponseEntity.ok(new ApiResponse("Danh sách địa chỉ", true, addresses));
    }

    @PostMapping("/user/address")
    public ResponseEntity<ApiResponse> createOrUpdateAddress(@Valid @RequestBody AddressRequestDto addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy tên người dùng từ token

        boolean isUpdated = addressService.createOrUpdateAddress(username, addressRequest);

        String message = isUpdated ? "Cập nhật địa chỉ thành công" : "Thêm mới địa chỉ thành công";
        return ResponseEntity.ok(new ApiResponse(message, true));
    }


}
