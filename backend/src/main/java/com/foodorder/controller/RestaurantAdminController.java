package com.foodorder.controller;

import com.foodorder.dto.CommonDtos.MessageResponse;
import com.foodorder.dto.FoodItemDtos.FoodItemAvailabilityRequest;
import com.foodorder.dto.FoodItemDtos.FoodItemRequest;
import com.foodorder.dto.FoodItemDtos.FoodItemResponse;
import com.foodorder.dto.RestaurantDtos.RestaurantRequest;
import com.foodorder.dto.RestaurantDtos.RestaurantResponse;
import com.foodorder.entity.User;
import com.foodorder.service.CurrentUserService;
import com.foodorder.service.FoodItemService;
import com.foodorder.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurant-admin")
@RequiredArgsConstructor
public class RestaurantAdminController {

    private final RestaurantService restaurantService;
    private final FoodItemService foodItemService;
    private final CurrentUserService currentUserService;

    // ---- Restaurant profile ----
    @PostMapping("/restaurant")
    public RestaurantResponse createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        return restaurantService.createForOwner(currentUserService.getCurrentUser(), request);
    }

    @GetMapping("/restaurant")
    public RestaurantResponse getMyRestaurant() {
        return restaurantService.getOwnRestaurantResponse(currentUserService.getCurrentUser());
    }

    @PutMapping("/restaurant")
    public RestaurantResponse updateRestaurant(@Valid @RequestBody RestaurantRequest request) {
        return restaurantService.updateOwnRestaurant(currentUserService.getCurrentUser(), request);
    }

    @PatchMapping("/restaurant/open-status")
    public RestaurantResponse setOpenStatus(@RequestBody Map<String, Boolean> body) {
        boolean open = Boolean.TRUE.equals(body.get("open"));
        return restaurantService.setOpenStatus(currentUserService.getCurrentUser(), open);
    }

    // ---- Menu management ----
    @GetMapping("/menu")
    public List<FoodItemResponse> getMyMenu() {
        return foodItemService.getOwnMenu(currentUserService.getCurrentUser());
    }

    @PostMapping("/menu")
    public FoodItemResponse addFoodItem(@Valid @RequestBody FoodItemRequest request) {
        return foodItemService.addItem(currentUserService.getCurrentUser(), request);
    }

    @PutMapping("/menu/{itemId}")
    public FoodItemResponse updateFoodItem(@PathVariable Long itemId, @Valid @RequestBody FoodItemRequest request) {
        return foodItemService.updateItem(currentUserService.getCurrentUser(), itemId, request);
    }

    @PatchMapping("/menu/{itemId}/availability")
    public FoodItemResponse setAvailability(@PathVariable Long itemId,
                                             @Valid @RequestBody FoodItemAvailabilityRequest request) {
        User owner = currentUserService.getCurrentUser();
        return foodItemService.setAvailability(owner, itemId, request.available());
    }

    @DeleteMapping("/menu/{itemId}")
    public MessageResponse deleteFoodItem(@PathVariable Long itemId) {
        foodItemService.deleteItem(currentUserService.getCurrentUser(), itemId);
        return new MessageResponse("Food item deleted");
    }
}
