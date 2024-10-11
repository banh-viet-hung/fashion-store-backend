package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends JpaRepository<User, Long> {

    // Phương thức kiểm tra xem email đã tồn tại trong cơ sở dữ liệu hay chưa
    boolean existsByEmail(String email);

    // Phương thức tìm người dùng theo email
    User findByEmail(String email);
}
