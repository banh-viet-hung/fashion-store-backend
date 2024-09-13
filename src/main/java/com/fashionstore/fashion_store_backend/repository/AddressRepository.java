package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "address", path = "address")
public interface AddressRepository extends JpaRepository<Address, Long> {
}
