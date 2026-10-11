package com.foodorder.repository;

import com.foodorder.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByRestaurantId(Long restaurantId);
    List<FoodItem> findByNameContainingIgnoreCaseAndAvailableTrue(String keyword);
    List<FoodItem> findByCategoryId(Long categoryId);
}
