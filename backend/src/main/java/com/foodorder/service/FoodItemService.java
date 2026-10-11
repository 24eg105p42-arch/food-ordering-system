package com.foodorder.service;

import com.foodorder.dto.FoodItemDtos.FoodItemRequest;
import com.foodorder.dto.FoodItemDtos.FoodItemResponse;
import com.foodorder.entity.Category;
import com.foodorder.entity.FoodItem;
import com.foodorder.entity.Restaurant;
import com.foodorder.entity.User;
import com.foodorder.exception.ResourceNotFoundException;
import com.foodorder.repository.CategoryRepository;
import com.foodorder.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantService restaurantService;

    public List<FoodItemResponse> getMenuForRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId).stream().map(this::toResponse).toList();
    }

    public List<FoodItemResponse> search(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(keyword)
                .stream().map(this::toResponse).toList();
    }

    public FoodItem findOrThrow(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
    }

    public FoodItemResponse addItem(User owner, FoodItemRequest request) {
        Restaurant restaurant = restaurantService.getOwnRestaurantOrThrow(owner);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        FoodItem item = FoodItem.builder()
                .restaurant(restaurant)
                .category(category)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .veg(request.veg())
                .available(true)
                .build();

        return toResponse(foodItemRepository.save(item));
    }

    public FoodItemResponse updateItem(User owner, Long itemId, FoodItemRequest request) {
        FoodItem item = getOwnedItemOrThrow(owner, itemId);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        item.setVeg(request.veg());
        item.setCategory(category);

        return toResponse(foodItemRepository.save(item));
    }

    public FoodItemResponse setAvailability(User owner, Long itemId, boolean available) {
        FoodItem item = getOwnedItemOrThrow(owner, itemId);
        item.setAvailable(available);
        return toResponse(foodItemRepository.save(item));
    }

    public void deleteItem(User owner, Long itemId) {
        FoodItem item = getOwnedItemOrThrow(owner, itemId);
        foodItemRepository.delete(item);
    }

    public List<FoodItemResponse> getOwnMenu(User owner) {
        Restaurant restaurant = restaurantService.getOwnRestaurantOrThrow(owner);
        return getMenuForRestaurant(restaurant.getId());
    }

    private FoodItem getOwnedItemOrThrow(User owner, Long itemId) {
        Restaurant restaurant = restaurantService.getOwnRestaurantOrThrow(owner);
        FoodItem item = findOrThrow(itemId);
        if (!item.getRestaurant().getId().equals(restaurant.getId())) {
            throw new ResourceNotFoundException("Food item not found in your restaurant");
        }
        return item;
    }

    private FoodItemResponse toResponse(FoodItem f) {
        return new FoodItemResponse(f.getId(), f.getName(), f.getDescription(), f.getPrice(),
                f.getImageUrl(), f.isVeg(), f.isAvailable(),
                f.getCategory().getName(), f.getRestaurant().getId(), f.getRestaurant().getName());
    }
}
