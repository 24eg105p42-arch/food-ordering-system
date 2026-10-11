package com.foodorder.controller;

import com.foodorder.dto.CategoryDtos.CategoryResponse;
import com.foodorder.dto.FoodItemDtos.FoodItemResponse;
import com.foodorder.dto.RestaurantDtos.RestaurantResponse;
import com.foodorder.dto.ReviewDtos.ReviewResponse;
import com.foodorder.service.CategoryService;
import com.foodorder.service.FoodItemService;
import com.foodorder.service.RestaurantService;
import com.foodorder.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Everything here is readable without logging in: browsing is open to visitors. */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final RestaurantService restaurantService;
    private final FoodItemService foodItemService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping("/restaurants")
    public List<RestaurantResponse> getRestaurants() {
        return restaurantService.getApprovedRestaurants();
    }

    @GetMapping("/restaurants/{id}")
    public RestaurantResponse getRestaurant(@PathVariable Long id) {
        return restaurantService.getById(id);
    }

    @GetMapping("/restaurants/{id}/menu")
    public List<FoodItemResponse> getMenu(@PathVariable Long id) {
        return foodItemService.getMenuForRestaurant(id);
    }

    @GetMapping("/food-items/search")
    public List<FoodItemResponse> searchFood(@RequestParam String keyword) {
        return foodItemService.search(keyword);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/food-items/{id}/reviews")
    public List<ReviewResponse> getReviews(@PathVariable Long id) {
        return reviewService.getForFoodItem(id);
    }
}
