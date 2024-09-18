package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "feature", path = "feature")
public interface FeatureRepository extends JpaRepository<Feature, Long> {
}
