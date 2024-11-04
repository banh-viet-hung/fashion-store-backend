package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Address;
import com.fashionstore.fashion_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user); // Tìm địa chỉ theo người dùng
}
