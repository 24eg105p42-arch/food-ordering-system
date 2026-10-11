package com.foodorder.repository;

import com.foodorder.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFoodItemId(Long foodItemId);
    boolean existsByUserIdAndFoodItemIdAndOrderId(Long userId, Long foodItemId, Long orderId);
}
