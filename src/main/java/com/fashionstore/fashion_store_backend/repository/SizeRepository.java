package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "size", path = "size")
public interface SizeRepository extends JpaRepository<Size, Long> {
    Size findByName(String name);

    // Tìm các kích thước có tên nằm trong danh sách tên
    List<Size> findByNameIn(List<String> names);
}
