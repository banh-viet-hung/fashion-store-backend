package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
