package com.foodorder.repository;

import com.foodorder.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByApprovedTrue();
    List<Restaurant> findByOwnerId(Long ownerId);
    Optional<Restaurant> findFirstByOwnerId(Long ownerId);
}
