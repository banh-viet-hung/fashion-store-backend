package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Address;
import com.fashionstore.fashion_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user); // Tìm địa chỉ theo người dùng

    // Tìm địa chỉ mặc định hoặc không mặc định của người dùng
    Optional<Address> findByUserAndDefaultAddress(User user, boolean defaultAddress);
}
