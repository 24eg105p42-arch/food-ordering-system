package com.foodorder.controller;

import com.foodorder.dto.OrderDtos.OrderResponse;
import com.foodorder.dto.OrderDtos.OrderStatusUpdateRequest;
import com.foodorder.entity.Restaurant;
import com.foodorder.entity.User;
import com.foodorder.service.CurrentUserService;
import com.foodorder.service.OrderService;
import com.foodorder.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant-admin/orders")
@RequiredArgsConstructor
public class RestaurantOrderController {

    private final OrderService orderService;
    private final RestaurantService restaurantService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<OrderResponse> getIncomingOrders() {
        User owner = currentUserService.getCurrentUser();
        Restaurant restaurant = restaurantService.getOwnRestaurantOrThrow(owner);
        return orderService.getIncomingOrders(restaurant);
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusUpdateRequest request) {
        User owner = currentUserService.getCurrentUser();
        Restaurant restaurant = restaurantService.getOwnRestaurantOrThrow(owner);
        return orderService.updateStatus(restaurant, orderId, request.status());
    }
}
