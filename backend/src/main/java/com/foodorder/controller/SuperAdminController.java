package com.foodorder.controller;

import com.foodorder.dto.AdminDtos.RevenueStatsResponse;
import com.foodorder.dto.AdminDtos.UserSummaryResponse;
import com.foodorder.dto.CategoryDtos.CategoryRequest;
import com.foodorder.dto.CategoryDtos.CategoryResponse;
import com.foodorder.dto.OrderDtos.OrderResponse;
import com.foodorder.dto.RestaurantDtos.RestaurantResponse;
import com.foodorder.service.AdminService;
import com.foodorder.service.CategoryService;
import com.foodorder.service.OrderService;
import com.foodorder.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final AdminService adminService;
    private final RestaurantService restaurantService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    // ---- Users ----
    @GetMapping("/users")
    public List<UserSummaryResponse> getUsers() {
        return adminService.getAllUsers();
    }

    @PatchMapping("/users/{id}/active")
    public UserSummaryResponse setUserActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return adminService.setUserActive(id, Boolean.TRUE.equals(body.get("active")));
    }

    // ---- Restaurants ----
    @GetMapping("/restaurants")
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllForAdmin();
    }

    @PutMapping("/restaurants/{id}/approve")
    public RestaurantResponse approveRestaurant(@PathVariable Long id) {
        return restaurantService.approve(id);
    }

    // ---- Categories ----
    @GetMapping("/categories")
    public List<CategoryResponse> getCategories() {
        return categoryService.getAll();
    }

    @PostMapping("/categories")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/categories/{id}")
    public CategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }

    // ---- Orders & stats ----
    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/stats")
    public RevenueStatsResponse getStats() {
        return adminService.getStats();
    }
}
