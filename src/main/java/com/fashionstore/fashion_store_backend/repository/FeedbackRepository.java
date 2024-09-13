package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "feedback", path = "feedback")
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
