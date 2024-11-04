package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.AddressRequestDto;
import com.fashionstore.fashion_store_backend.dto.AddressResponseDto;
import com.fashionstore.fashion_store_backend.model.Address;
import com.fashionstore.fashion_store_backend.model.User;
import com.fashionstore.fashion_store_backend.repository.AddressRepository;
import com.fashionstore.fashion_store_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AddressResponseDto> getUserAddresses(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        List<Address> addresses = user.getAddresses(); // Giả sử bạn đã thiết lập mối quan hệ giữa User và Address
        return addresses.stream().map(address -> new AddressResponseDto(address.getId(), address.getFullName(), address.getPhoneNumber(), address.getAddress(), address.getCity(), address.getDistrict(), address.getWard(), address.isDefaultAddress())).collect(Collectors.toList());
    }

    public boolean createOrUpdateAddress(String username, AddressRequestDto addressRequest) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }

        List<Address> existingAddresses = addressRepository.findByUser(user);

        // Kiểm tra địa chỉ theo trạng thái defaultAddress
        Address existingAddress = existingAddresses.stream().filter(address -> address.isDefaultAddress() == addressRequest.isDefaultAddress()).findFirst().orElse(null);

        if (existingAddress != null) {
            // Nếu địa chỉ đã tồn tại, cập nhật thông tin
            existingAddress.setFullName(addressRequest.getFullName());
            existingAddress.setPhoneNumber(addressRequest.getPhoneNumber());
            existingAddress.setAddress(addressRequest.getAddress());
            existingAddress.setCity(addressRequest.getCity());
            existingAddress.setDistrict(addressRequest.getDistrict());
            existingAddress.setWard(addressRequest.getWard());
            addressRepository.save(existingAddress);
            return true; // Cập nhật thành công
        } else {
            // Nếu địa chỉ không tồn tại, tạo địa chỉ mới
            Address newAddress = new Address();
            newAddress.setFullName(addressRequest.getFullName());
            newAddress.setPhoneNumber(addressRequest.getPhoneNumber());
            newAddress.setAddress(addressRequest.getAddress());
            newAddress.setCity(addressRequest.getCity());
            newAddress.setDistrict(addressRequest.getDistrict());
            newAddress.setWard(addressRequest.getWard());
            newAddress.setDefaultAddress(addressRequest.isDefaultAddress());
            newAddress.setUser(user);
            addressRepository.save(newAddress);
            return false; // Thêm mới thành công
        }
    }


}
