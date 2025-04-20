package com.fashionstore.fashion_store_backend.repository;

import com.fashionstore.fashion_store_backend.model.Feedback;
import com.fashionstore.fashion_store_backend.model.Product;
import com.fashionstore.fashion_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "feedback", path = "feedback")
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProductIdAndIsPublicTrue(Long productId);
    List<Feedback> findByUser(User user);
    List<Feedback> findByProductAndUser(Product product, User user);
    List<Feedback> findByProductAndUserAndSizeAndColor(Product product, User user, String size, String color);
}
