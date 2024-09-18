package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "size", path = "size")
public interface SizeRepository extends JpaRepository<Size, Long> {

}
